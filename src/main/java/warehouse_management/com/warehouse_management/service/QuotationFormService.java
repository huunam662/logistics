package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.*;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.*;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.QuotationType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.QuotationFormMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.QuotationForm;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.quotation_form.QuotationFormRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationFormService {

    private final QuotationFormRepository quotationFormRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final QuotationFormMapper quotationFormMapper;
    private final CustomAuthentication customAuthentication;

    public QuotationForm getToId(ObjectId id){

        QuotationForm quotationForm = quotationFormRepository.findById(id).orElse(null);

        if(quotationForm == null || quotationForm.getDeletedAt() != null)
            throw LogicErrException.of("Mẫu báo giá không tồn tại.");

        return quotationForm;
    }

    @Transactional
    public void softDeleteQuotationForm(ObjectId id){

        QuotationForm quotationForm = getToId(id);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        quotationFormRepository.softDelete(id, customUserDetail.getEmail());
    }

    @Transactional
    public void bulkSoftDeleteQuotationForm(BulkDeleteQuotationDto dto){

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        List<ObjectId> quotationIdsToDelete = dto.getQuotationIds()
                                .stream()
                                .map(ObjectId::new)
                                .toList();

        quotationFormRepository.bulkSoftDelete(quotationIdsToDelete, customUserDetail.getEmail());
    }

    public QuotationFormDto getQuotationFormDtoToId(ObjectId id){

        return quotationFormRepository.findQuotationFormDtoById(id)
                .orElseThrow(() -> LogicErrException.of("Đơn báo giá không tồn tại"));
    }

    public Page<QuotationProductWarehouseDto> getPageQuotationProductWarehouse(PageOptionsDto optionsReq) {
        return inventoryItemRepository.findPageQuotationProductWarehouse(optionsReq);
    }

    public Page<QuotationSparePartWarehouseDto> getPageQuotationSparePartWarehouse(PageOptionsDto optionsReq) {
        return inventoryItemRepository.findPageQuotationSparePartWarehouse(optionsReq);
    }

    @Transactional
    public QuotationForm createQuotationForm(CreateQuotationFormDto dto) {

        if(quotationFormRepository.existsByQuotationCode(dto.getQuotationCode()))
            throw LogicErrException.of("Mã báo giá '" + dto.getQuotationCode() + "' đã tồn tại.");

        QuotationForm quotationForm = quotationFormMapper.toQuotationForm(dto);

        return quotationFormRepository.save(quotationForm);
    }

    @Transactional
    public QuotationForm updateQuotationForm(UpdateQuotationFormDto dto) {

        QuotationForm quotationForm = getToId(new ObjectId(dto.getId()));

        if(!quotationForm.getQuotationCode().equals(dto.getQuotationCode()))
            if(quotationFormRepository.existsByQuotationCode(dto.getQuotationCode()))
                throw LogicErrException.of("Mã báo giá '" + dto.getQuotationCode() + "' đã tồn tại.");

        quotationForm = quotationFormMapper.toQuotationForm(dto);

        return quotationFormRepository.save(quotationForm);
    }

    @Transactional
    public QuotationForm fillProductToQuotationForm(FillProductForQuotationDto dto) {

        QuotationForm quotationForm = getToId(new ObjectId(dto.getQuotationFormId()));

        if(quotationForm.getQuotationInventoryItems() == null)
            quotationForm.setQuotationInventoryItems(new ArrayList<>());

        if(!dto.getQuotationProducts().isEmpty()){

            updateQuotationProductExistsInForm(quotationForm, dto);

            pushQuotationProductToForm(quotationForm, dto);
        }

        if(!dto.getQuotationProductManuals().isEmpty()){
            for(var quotationProductManual : dto.getQuotationProductManuals()){
                QuotationForm.InventoryItemQuotation quotationToPush = quotationFormMapper.toInventoryItemQuotation(quotationProductManual);
                quotationToPush.setId(new ObjectId());
                quotationToPush.setQuotationType(QuotationType.MANUAL.getId());
                quotationForm.getQuotationInventoryItems().add(quotationToPush);
            }
        }

        return quotationFormRepository.save(quotationForm);
    }

    protected void updateQuotationProductExistsInForm(QuotationForm quotationForm, FillProductForQuotationDto dto){
        for(var quotationProductItem : quotationForm.getQuotationInventoryItems()){
            FillProductForQuotationDto.QuotationProduct productExists = dto.getQuotationProducts()
                    .stream()
                    .filter(e -> quotationProductItem.getId().equals(new ObjectId(e.getId())))
                    .findFirst()
                    .orElse(null);

            if(productExists != null){
                quotationProductItem.setQuantity(productExists.getQuantity());
                quotationProductItem.setSalePrice(productExists.getSalePrice());
                dto.getQuotationProducts().remove(productExists);
            }
        }
    }

    protected void pushQuotationProductToForm(QuotationForm quotationForm, FillProductForQuotationDto dto){
        List<ObjectId> productIdListToQuery = dto.getQuotationProducts()
                .stream()
                .map(e -> new ObjectId(e.getId()))
                .toList();

        if(!productIdListToQuery.isEmpty()){
            Map<ObjectId, InventoryItem> productsQuotationMap = inventoryItemRepository.findByIdIn(productIdListToQuery)
                    .stream()
                    .collect(Collectors.toMap(InventoryItem::getId, e -> e));

            for(var productDtoToPush : dto.getQuotationProducts()){
                InventoryItem product = productsQuotationMap.getOrDefault(new ObjectId(productDtoToPush.getId()), null);
                if(product != null){
                    QuotationForm.InventoryItemQuotation quotationProduct = quotationFormMapper.toInventoryItemQuotation(product);
                    quotationProduct.setQuantity(productDtoToPush.getQuantity());
                    quotationProduct.setSalePrice(productDtoToPush.getSalePrice());
                    quotationProduct.setQuotationType(QuotationType.FROM_WAREHOUSE.getId());
                    quotationForm.getQuotationInventoryItems().add(quotationProduct);
                }
            }
        }
    }

    @Transactional
    public QuotationForm fillSparePartToQuotationForm(FillSparePartForQuotationDto dto) {

        QuotationForm quotationForm = getToId(new ObjectId(dto.getQuotationFormId()));

        if(quotationForm.getQuotationInventoryItems() == null)
            quotationForm.setQuotationInventoryItems(new ArrayList<>());

        if(!dto.getQuotationSpareParts().isEmpty()){

            updateQuotationSparePartExistsInForm(quotationForm, dto);

            pushQuotationSparePartToForm(quotationForm, dto);
        }

        if(!dto.getQuotationSparePartManuals().isEmpty()){
            for(var quotationSparePartManual : dto.getQuotationSparePartManuals()){
                QuotationForm.InventoryItemQuotation quotationToPush = quotationFormMapper.toInventoryItemQuotation(quotationSparePartManual);
                quotationToPush.setId(new ObjectId());
                quotationToPush.setInventoryType(InventoryType.SPARE_PART.getId());
                quotationToPush.setQuotationType(QuotationType.MANUAL.getId());
                quotationForm.getQuotationInventoryItems().add(quotationToPush);
            }
        }

        return quotationFormRepository.save(quotationForm);
    }

    protected void updateQuotationSparePartExistsInForm(QuotationForm quotationForm, FillSparePartForQuotationDto dto){
        for(var quotationSparePartItem : quotationForm.getQuotationInventoryItems()){
            FillSparePartForQuotationDto.QuotationSparePart sparePartExists = dto.getQuotationSpareParts()
                    .stream()
                    .filter(e -> quotationSparePartItem.getId().equals(new ObjectId(e.getId())))
                    .findFirst()
                    .orElse(null);

            if(sparePartExists != null){
                quotationSparePartItem.setQuantity(sparePartExists.getQuantity());
                quotationSparePartItem.setSalePrice(sparePartExists.getSalePrice());
                dto.getQuotationSpareParts().remove(sparePartExists);
            }
        }
    }

    protected void pushQuotationSparePartToForm(QuotationForm quotationForm, FillSparePartForQuotationDto dto){
        List<ObjectId> sparePartIdListToQuery = dto.getQuotationSpareParts()
                .stream()
                .map(e -> new ObjectId(e.getId()))
                .toList();

        if(!sparePartIdListToQuery.isEmpty()){
            Map<ObjectId, InventoryItem> sparePartsQuotationMap = inventoryItemRepository.findByIdIn(sparePartIdListToQuery)
                    .stream()
                    .collect(Collectors.toMap(InventoryItem::getId, e -> e));

            for(var sparePartDtoToPush : dto.getQuotationSpareParts()){
                InventoryItem sparePart = sparePartsQuotationMap.getOrDefault(new ObjectId(sparePartDtoToPush.getId()), null);
                if(sparePart != null){
                    QuotationForm.InventoryItemQuotation sparePartQuotation = quotationFormMapper.toInventoryItemQuotation(sparePart);
                    sparePartQuotation.setQuantity(sparePartDtoToPush.getQuantity());
                    sparePartQuotation.setSalePrice(sparePartDtoToPush.getSalePrice());
                    sparePartQuotation.setQuotationType(QuotationType.FROM_WAREHOUSE.getId());
                    quotationForm.getQuotationInventoryItems().add(sparePartQuotation);
                }
            }
        }
    }

    public List<QuotationFormProductDto> getQuotationProductToQuotationId(ObjectId quotationFormId){

        QuotationForm quotationForm = getToId(quotationFormId);

        if(quotationForm.getQuotationInventoryItems() == null) return new ArrayList<>();

        return quotationForm.getQuotationInventoryItems()
                .stream()
                .filter(e -> !InventoryType.SPARE_PART.getId().equals(e.getInventoryType()))
                .map(quotationFormMapper::toQuotationProductDto)
                .toList();
    }

    public List<QuotationFormSparePartDto> getQuotationSparePartToQuotationId(ObjectId quotationFormId){

        QuotationForm quotationForm = getToId(quotationFormId);

        if(quotationForm.getQuotationInventoryItems() == null) return new ArrayList<>();

        return quotationForm.getQuotationInventoryItems()
                .stream()
                .filter(e -> InventoryType.SPARE_PART.getId().equals(e.getInventoryType()))
                .map(quotationFormMapper::toQuotationSparePartDto)
                .toList();
    }

    public Page<QuotationFormPageDto> getPageQuotationForm(PageOptionsDto optionsDto){
        return quotationFormRepository.findPageQuotationForm(optionsDto);
    }
}
