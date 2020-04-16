package com.example.flowershop.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FlowersProvider extends ContentProvider {
    public static final String LOG_TAG = FlowersProvider.class.getSimpleName();
    private FlowersDbHelper mDbHelper;

    private static final int FLOWERS = 100;

    private static final int FLOWER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(FlowersContract.CONTENT_AUTHORITY, FlowersContract.PATH_FLOWERS, FLOWERS);

        sUriMatcher.addURI(FlowersContract.CONTENT_AUTHORITY, FlowersContract.PATH_FLOWERS + "/#", FLOWER_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new FlowersDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FLOWERS:
                cursor = database.query(FlowersContract.FlowersEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FLOWER_ID:

                selection = FlowersContract.FlowersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FlowersContract.FlowersEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FLOWERS:
                return FlowersContract.FlowersEntry.CONTENT_LIST_TYPE;
            case FLOWER_ID:
                return FlowersContract.FlowersEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FLOWERS:
                return insertFlower(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFlower(Uri uri, ContentValues values) {
        Integer type = values.getAsInteger(FlowersContract.FlowersEntry.COLUMN_FLOWERS_TYPE);
        if (type == null || !FlowersContract.FlowersEntry.isValidType(type)) {
            throw new IllegalArgumentException("Flowers requires valid type");
        }

        String name = values.getAsString(FlowersContract.FlowersEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Flowers requires a name");
        }

        String description = values.getAsString(FlowersContract.FlowersEntry.COLUMN_PRODUCT_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Flowers requires a description");
        }

        Double price = values.getAsDouble(FlowersContract.FlowersEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Flowers requires valid price");
        }

        Integer quantity = values.getAsInteger(FlowersContract.FlowersEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Flowers requires valid quantity");
        }

        String supplierName = values.getAsString(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Flowers requires a supplier name");
        }

        String supplierPhone = values.getAsString(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_PHONE_NUMBER).trim();
        supplierPhone = supplierPhone.replace(" ", "");
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Flowers requires a supplier name");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(FlowersContract.FlowersEntry.TABLE_NAME, null, values);
        Log.e(LOG_TAG, "The flower is saved with id: " + id);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (match) {
            case FLOWERS:
                rowsDeleted = database.delete(FlowersContract.FlowersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FLOWER_ID:
                selection = FlowersContract.FlowersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FlowersContract.FlowersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FLOWERS:
                return updateFlowers(uri, values, selection, selectionArgs);
            case FLOWER_ID:
                selection = FlowersContract.FlowersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFlowers(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateFlowers(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_FLOWERS_TYPE)) {
            Integer type = values.getAsInteger(FlowersContract.FlowersEntry.COLUMN_FLOWERS_TYPE);
            if (type == null || !FlowersContract.FlowersEntry.isValidType(type)) {
                throw new IllegalArgumentException("Flowers requires valid type");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(FlowersContract.FlowersEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Flowers requires a name");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_PRODUCT_DESCRIPTION)) {
            String description = values.getAsString(FlowersContract.FlowersEntry.COLUMN_PRODUCT_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Flowers requires a description");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(FlowersContract.FlowersEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Flowers requires valid price");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(FlowersContract.FlowersEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Flowers requires valid quantity");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Flowers requires a supplier name");
            }
        }

        if (values.containsKey(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhone = values.getAsString(FlowersContract.FlowersEntry.COLUMN_SUPPLIER_PHONE_NUMBER).trim();
            supplierPhone = supplierPhone.replace(" ", "");
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Flowers requires a supplier name");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(FlowersContract.FlowersEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
