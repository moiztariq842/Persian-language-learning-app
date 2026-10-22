package com.persianapp.dataaccesslayer.inmemory;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterComponent;
import com.persianapp.businesslayer.ComponentType;
import com.persianapp.businesslayer.GlossaryEntry;
import com.persianapp.dataaccesslayer.ChapterDao;
import com.persianapp.dataaccesslayer.ComponentRepository;

import java.util.UUID;

/**
 * Seeds realistic Persian and Urdu sample data for demonstration when running without a database.
 */
public class SampleDataSeeder {

    public static void seed(InMemoryChapterDao chapterDao,
                            InMemoryStructureAndComponentStore store,
                            InMemoryGlossaryEntryDAO glossaryDao) {

        try {
            // ── Chapter 1: Greetings & Introductions ──────────────
            String ch1Id = "ch-01-" + UUID.randomUUID().toString().substring(0, 8);
            Chapter ch1 = new Chapter(ch1Id, "فصل اول: تعارف و احوال پرسی (Greetings & Introductions)");
            chapterDao.insert(ch1);

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch1Id,
                    ComponentType.LEARNING_OBJECTIVES, 0,
                    "۱. احوال پرسی کی بنیادی فارسی عبارتیں سیکھنا۔\n" +
                    "۲. روزمرہ گفتگو میں تعارف کروانے کے طریقے سمجھنا۔\n" +
                    "۳. شائستہ گفتگو کے بنیادی آداب۔"
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch1Id,
                    ComponentType.PERSIAN_DIALOGUE, 1,
                    "سارا: سلام! روز شما بخیر.\n" +
                    "مریم: سلام! حال شما چطور است؟\n" +
                    "سارا: خیلی ممنون، من خوبم. شما چطورید؟\n" +
                    "مریم: تشکر، من هم خوبم. به ایران خوش آمدید!\n" +
                    "سارا: سپاسگزارم، از دیدار شما خوشحالم."
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch1Id,
                    ComponentType.URDU_EXPLANATION, 2,
                    "فارسی زبان میں ملاقات کے وقت 'سلام' اور 'حال شما چطور است؟' (آپ کا کیا حال ہے؟) " +
                    "سب سے زیادہ مستعمل عبارتیں ہیں۔ جواب میں 'خیلی ممنون، خوبم' (بہت شکریہ، میں ٹھیک ہوں) " +
                    "کہا جاتا ہے۔ 'روز بخیر' دن کے کسی بھی وقت خیرمقدمی کلمات کے طور پر استعمال ہو سکتا ہے۔"
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch1Id,
                    ComponentType.EXAMPLES, 3,
                    "مثال ۱: سلام، صبح بخیر! (ہیلو، صبح بخیر!)\n" +
                    "مثال ۲: حال شما چطور است؟ — من خوبم، متشکرم.\n" +
                    "مثال ۳: از آشنایی با شما خوشوقتم. (آپ سے مل کر خوشی ہوئی۔)"
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch1Id,
                    ComponentType.GLOSSARY, 4,
                    ""
            ));

            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "سلام", "سلام / ہیلو"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "روز بخیر", "دن بخیر / اچھا دن"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "حال شما چطور است؟", "آپ کا کیا حال ہے؟"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "خیلی ممنون", "بہت شکریہ"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "خوش آمدید", "خوش آمدید / جی آیاں نوں"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch1Id, "سپاسگزارم", "میں شکر گزار ہوں"));

            // ── Chapter 2: Numbers & Counting ─────────────────────
            String ch2Id = "ch-02-" + UUID.randomUUID().toString().substring(0, 8);
            Chapter ch2 = new Chapter(ch2Id, "فصل دوم: اعداد و گنتی (Persian Numbers)");
            chapterDao.insert(ch2);

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch2Id,
                    ComponentType.LEARNING_OBJECTIVES, 0,
                    "۱. ایک سے دس تک فارسی گنتی یاد کرنا۔\n" +
                    "۲. اعداد کا درست تلفظ اور املا سیکھنا۔"
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch2Id,
                    ComponentType.EXAMPLES, 1,
                    "۱ : یک (ایک)\n" +
                    "۲ : دو (دو)\n" +
                    "۳ : سه (تین)\n" +
                    "۴ : چهار (چار)\n" +
                    "۵ : پنج (پانچ)\n" +
                    "۶ : شش (چھ)\n" +
                    "۷ : هفت (سات)\n" +
                    "۸ : هشت (آٹھ)\n" +
                    "۹ : نه (نو)\n" +
                    "۱۰ : ده (دس)"
            ));

            store.add(new ChapterComponent(
                    UUID.randomUUID().toString(), ch2Id,
                    ComponentType.GLOSSARY, 2,
                    ""
            ));

            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch2Id, "یک", "ایک (1)"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch2Id, "دو", "دو (2)"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch2Id, "سه", "تین (3)"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch2Id, "چهار", "چار (4)"));
            glossaryDao.addEntry(new GlossaryEntry(UUID.randomUUID().toString(), ch2Id, "پنج", "پانچ (5)"));

        } catch (Exception ignored) {}
    }
}
