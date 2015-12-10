package com.jacky.commondraw.model;

/**
 * Created by $ zhoudeheng on 2015/12/8.
 * Email zhoudeheng@qccr.com
 */
public  interface IPropertyValueChangedListener {
     void onPropertyValeChanged(
            InsertableObjectBase insertableObject, int propertyId,
            Object oldValue, Object newValue, boolean fromUndoRedo);

     void onExtraPropertyValueAdded(
            InsertableObjectBase insertableObjec, String key,
            boolean fromUndoRedo);

     void onExtraPropertyValueDeleted(
            InsertableObjectBase insertableObjec, String key,
            boolean fromUndoRedo);

     void onExtraPropertyValueChanged(
            InsertableObjectBase insertableObjec, String key,
            Object oldValue, Object newValue, boolean fromUndoRedo);
}
