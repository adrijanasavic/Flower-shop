package com.example.flowershop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class FlowersContract {
    private FlowersContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.flowershop";
    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY );
    public static final String PATH_FLOWERS = "flowers";

    public static final class FlowersEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath( BASE_CONTENT_URI, PATH_FLOWERS );


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOWERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOWERS;

        public final static String TABLE_NAME = "flowers";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_FLOWERS_TYPE = "flowers_type";

        public final static String COLUMN_PRODUCT_NAME = "product_name";

        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";

        public final static String COLUMN_PRICE = "price";

        public final static String COLUMN_QUANTITY = "quantity";

        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";


        public static final int TYPE_CALLA = 0;
        public static final int TYPE_ROSE = 1;
        public static final int TYPE_ORCHIDS = 2;
        public static final int TYPE_WEDDING = 3;
        public static final int TYPE_ROMANTIC = 4;


        public static boolean isValidType(int type) {
            if (type == TYPE_CALLA || type == TYPE_ROSE || type == TYPE_ORCHIDS
                    || type == TYPE_WEDDING || type == TYPE_ROMANTIC) {
                return true;
            }
            return false;
        }
    }
}
