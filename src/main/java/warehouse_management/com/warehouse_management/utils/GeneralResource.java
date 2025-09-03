package warehouse_management.com.warehouse_management.utils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.enumerate.WarehouseSubTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Component
public class GeneralResource {
    //    CONSTANT
    public static final int PXK_PNK_DATASET_ROW_IDX = 17;
    public static final int PXKDCNB_DATASET_ROW_IDX = 18;




}
