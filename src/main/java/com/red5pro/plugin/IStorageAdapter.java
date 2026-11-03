package com.red5pro.plugin;

import java.util.List;

/**
 * Adapter for uploading, listing and deleting files on a storage service.
 */
public interface IStorageAdapter {
    /**
     * This method is called to upload a list of files to the storage service.
     *
     * @param bucketName name of the target bucket
     * @param filePaths Full paths of files to be uploaded.
     * @return true on success
     */
    boolean uploadFiles(String bucketName, String... filePaths);

    /**
     * This method is called to list files on the storage service.
     *
     * @param bucketName name of the bucket to list
     * @return list of files
     */
    List<String> listFiles(String bucketName);

    /**
     * This method is called to delete files on the storage service.
     *
     * @param bucketName name of the bucket holding the files
     * @param objectKeys keys of the objects to delete
     * @return list of deleted files
     */
    List<String> deleteFiles(String bucketName, String... objectKeys);

}
