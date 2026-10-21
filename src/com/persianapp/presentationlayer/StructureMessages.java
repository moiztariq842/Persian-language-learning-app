package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.StructureErrorCode;

/** Urdu messages shown to the administrator for UC-02 outcomes (NFR-US-01, NFR-US-06). */
public final class StructureMessages {

    public static final String SAVED = "باب کی ساخت محفوظ ہو گئی";
    public static final String UNEXPECTED = "درخواست مکمل نہیں ہو سکی";
    public static final String STORAGE_FAILED = "ڈیٹا بیس سے رابطہ نہیں ہو سکا، دوبارہ کوشش کریں";
    public static final String CONFIRM_REMOVE_ALL = "حصے ہٹا دیے جائیں گے، کیا آپ یقین رکھتے ہیں؟";
    public static final String CONFIRM_TITLE = "تصدیق";
    public static final String CONFIRM_YES = "ہاں، ہٹا دیں";
    public static final String CONFIRM_NO = "نہیں";
    public static final String SAVE_CANCELLED = "محفوظ کرنا منسوخ کر دیا گیا";

    private StructureMessages() {
    }

    public static String forCode(StructureErrorCode code) {
        switch (code) {
        case BLANK_CHAPTER_ID:
            return "باب کی شناخت درج کرنا ضروری ہے";
        case CHAPTER_NOT_FOUND:
            return "یہ باب موجود نہیں ہے";
        case NULL_COMPONENT_LIST:
            return "حصوں کی فہرست درکار ہے";
        case NULL_COMPONENT:
            return "فہرست میں غلط حصہ موجود ہے";
        case DUPLICATE_COMPONENT:
            return "ایک ہی حصہ ایک سے زیادہ بار منتخب نہیں کیا جا سکتا";
        case COMPONENT_NOT_SELECTED:
            return "یہ حصہ باب میں منتخب نہیں ہے";
        case POSITION_OUT_OF_RANGE:
            return "منتخب مقام درست نہیں ہے";
        default:
            return UNEXPECTED;
        }
    }
}