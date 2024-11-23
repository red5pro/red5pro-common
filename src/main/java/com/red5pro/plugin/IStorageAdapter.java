package com.red5pro.plugin;

import java.util.List;

public interface IStorageAdapter {
    /**
     * This method is called to upload a list of files to the storage service.
     *
     * @param bucketName
     * @param filePaths Full paths of files to be uploaded.
     * @return true on success
     */
    boolean uploadFiles(String bucketName, String... filePaths);

    /**
     * This method is called to list files on the storage service.
     *
     * @param bucketName
     * @return list of files
     */
    List<String> listFiles(String bucketName);

    /**
     * This method is called to delete files on the storage service.
     *
     * @param bucketName
     * @param objectKeys
     * @return list of deleted files
     */
    List<String> deleteFiles(String bucketName, String... objectKeys);

}
