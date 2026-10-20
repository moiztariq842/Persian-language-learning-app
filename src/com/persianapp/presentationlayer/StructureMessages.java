package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.StructureErrorCode;

/** Urdu messages shown to the administrator for UC-02 outcomes (NFR-US-01, NFR-US-06). */
public final class StructureMessages {

    public static final String SAVED = "باب کی ساخت محفوظ ہو گئی";
    public static final String UNEXPECTED = "درخواست مکمل نہیں ہو سکی";
    public static final String STORAGE_FAILED = "ڈیٹا بیس سے رابطہ نہیں ہو سکا، دوبارہ کوشش کریں";

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
        case EMPTY_SELECTION:
            return "باب کے لیے کم از کم ایک حصہ منتخب کریں";
        case COMPONENT_NOT_SELECTED:
            return "یہ حصہ باب میں منتخب نہیں ہے";
        case POSITION_OUT_OF_RANGE:
            return "منتخب مقام درست نہیں ہے";
        default:
            return UNEXPECTED;
        }
    }
}