package com.persianapp.presentationlayer;

import com.persianapp.businesslayer.Chapter;
import com.persianapp.businesslayer.ChapterBao;
import com.persianapp.businesslayer.ChapterException;
import com.persianapp.businesslayer.DataAccessException;
import java.util.List;

// Facade controller mediating between ChapterPanel and the business layer (UC-01).
public class ChapterController {

    private final ChapterBao bao;

    public ChapterController(ChapterBao bao) {
        if (bao == null) {
            throw new IllegalArgumentException("ChapterBao is required.");
        }
        this.bao = bao;
    }

    public List<Chapter> listChapters() { return bao.getAllChapters(); }

    public ActionResult createChapter(String title) {
        try {
            bao.createChapter(title);
            return ActionResult.ok(ChapterMessages.CREATED);
        } catch (ChapterException e)    { return ActionResult.error(ChapterMessages.forCode(e.getCode())); }
        catch (DataAccessException e)   { return ActionResult.error(ChapterMessages.STORAGE_FAILED); }
        catch (Exception e)             { return ActionResult.error(ChapterMessages.UNEXPECTED); }
    }

    public ActionResult renameChapter(String chapterId, String newTitle) {
        try {
            bao.renameChapter(chapterId, newTitle);
            return ActionResult.ok(ChapterMessages.RENAMED);
        } catch (ChapterException e)    { return ActionResult.error(ChapterMessages.forCode(e.getCode())); }
        catch (DataAccessException e)   { return ActionResult.error(ChapterMessages.STORAGE_FAILED); }
        catch (Exception e)             { return ActionResult.error(ChapterMessages.UNEXPECTED); }
    }

    public ActionResult deleteChapter(String chapterId) {
        try {
            bao.deleteChapter(chapterId);
            return ActionResult.ok(ChapterMessages.DELETED);
        } catch (ChapterException e)    { return ActionResult.error(ChapterMessages.forCode(e.getCode())); }
        catch (DataAccessException e)   { return ActionResult.error(ChapterMessages.STORAGE_FAILED); }
        catch (Exception e)             { return ActionResult.error(ChapterMessages.UNEXPECTED); }
    }

    public String getChapterTitle(String chapterId) { return bao.findChapter(chapterId).getTitle(); }

    public boolean chapterExists(String chapterId) {
        try { bao.findChapter(chapterId); return true; }
        catch (ChapterException e) { return false; }
    }
}
