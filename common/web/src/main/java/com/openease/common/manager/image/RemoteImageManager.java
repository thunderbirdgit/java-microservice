package com.openease.common.manager.image;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;

/**
 * Interface: Remote image manager
 *
 * @author Alan Czajkowski
 */
public interface RemoteImageManager {

  ImageCreateResponse create(ImageCreateRequest request) throws GeneralManagerException;

}
