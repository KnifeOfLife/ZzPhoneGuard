package com.example.zzphoneguard.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.util.*;
import com.example.zzphoneguard.mode.ContactBean;

/**
 * 读取联系人的工具类
 */
public class ReadContactsEngine {
    private static List<ContactBean>  contacts = new ArrayList<ContactBean>();
    public  static List<ContactBean> readContacts(Context context){
        getPhoneContacts(context);
        getSIMContacts(context);
        return contacts;
    }
    /*

 private static final String[] PHONES_PROJECTION = new String[]{
 ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
 ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
};
     */

    /**
     * 获取手机中的联系人信息
     * @param context
     */
    private static void getPhoneContacts(Context context){
        Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
        },null,null,null);
        if(phoneCursor!=null) {
            while (phoneCursor.moveToNext()) {
                ContactBean contact = new ContactBean();
                String phone = phoneCursor.getString(1);
                if (TextUtils.isEmpty(phone))
                    continue;
                String name = phoneCursor.getString(0);
                contact.setName(name);
                contact.setPhone(phone);
                contacts.add(contact);
            }
            phoneCursor.close();
        }
    }

    /**
     * 获取SIM卡中联系人信息
     * @param context
     */
    private static void getSIMContacts(Context context){
            Uri uri = Uri.parse("content://icc/adn");
            Cursor phoneCursor = context.getContentResolver().query(uri,new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
            },null,null,null);
            if(phoneCursor!=null){
                while(phoneCursor.moveToNext()){
                    ContactBean contact = new ContactBean();
                    String number = phoneCursor.getString(1);
                    if(TextUtils.isEmpty(number))
                        continue;
                    String name = phoneCursor.getString(0);
                    contact.setName(name);
                    contact.setPhone(number);
                    contacts.add(contact);
                }
                phoneCursor.close();
            }

    }
}
/*        Uri uriContacts=Uri.parse("content://com.android.contacts/contacts");
        Uri uriData=Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uriContacts,new String[]{"_id"},null,null,null);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                ContactBean contact = new ContactBean();
                String id = cursor.getString(0);
                Cursor cursor2 = context.getContentResolver().query(uriData, new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
                if (cursor2!=null) {
                    while (cursor2.moveToNext()) {
                        String data = cursor2.getString(0).trim();
                        String mimetype = cursor2.getString(1);
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        if (mimetype.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                contact.setName(data);
                            }


                        } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                contact.setPhone(data);
                            }

                        }
                    }
                    cursor2.close();
                    contacts.add(contact);
                }
            }
            cursor.close();
        }*/