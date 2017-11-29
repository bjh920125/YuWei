package com.bap.yuwei.entity.event;

/**
 * 删除照片event
 */
public class DeleteImageEvent {

    private String filePath;
    private int deleteIndex;

    public DeleteImageEvent() {
    }

    public DeleteImageEvent(Object filePath, int deleteIndex) {
        this.filePath = filePath.toString();
        this.deleteIndex = deleteIndex;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDeleteIndex() {
        return deleteIndex;
    }

    public void setDeleteIndex(int deleteIndex) {
        this.deleteIndex = deleteIndex;
    }
}
