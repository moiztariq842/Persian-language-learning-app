package com.persianapp.presentationlayer;

// Display messages for chapter management (UC-01).
public final class ChapterMessages {

    private ChapterMessages() {}

    public static final String CREATED        = "باب کامیابی سے بن گیا — Chapter created successfully.";
    public static final String RENAMED        = "عنوان کامیابی سے اپ ڈیٹ ہو گیا — Chapter renamed successfully.";
    public static final String DELETED        = "باب اور اس کے تمام حصے حذف کر دیے گئے — Chapter and all its content deleted.";
    public static final String STORAGE_FAILED = "ڈیٹا محفوظ کرنے میں خرابی — Storage error, please try again.";
    public static final String UNEXPECTED     = "ایک غیر متوقع خرابی پیش آئی — An unexpected error occurred.";

    public static final String CONFIRM_DELETE_TITLE = "حذف کی تصدیق — Confirm Delete";
    public static final String CONFIRM_YES    = "ہاں، حذف کریں — Yes, Delete";
    public static final String CONFIRM_NO     = "نہیں، منسوخ کریں — No, Cancel";
    public static final String SELECT_FIRST   = "براہ کرم پہلے فہرست سے ایک باب منتخب کریں — Please select a chapter first.";

    public static String forCode(com.persianapp.businesslayer.ChapterErrorCode code) {
        return switch (code) {
            case BLANK_TITLE       -> "باب کا عنوان خالی نہیں ہو سکتا — Chapter title must not be blank.";
            case CHAPTER_NOT_FOUND -> "باب نہیں ملا — Chapter not found.";
        };
    }
}
