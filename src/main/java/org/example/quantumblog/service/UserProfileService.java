package org.example.quantumblog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaol
 */
@Service
public interface UserProfileService {
    void changeAvatar(String username, MultipartFile avatar);

    byte[] getAvatar(String username);
}
