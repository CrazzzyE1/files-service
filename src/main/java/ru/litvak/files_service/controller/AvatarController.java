package ru.litvak.files_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.service.AvatarService;

import java.util.UUID;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<byte[]> loadAvatar(@PathVariable UUID userId,
                                             @RequestParam(required = false, defaultValue = "MEDIUM") SizeType size) {
        return avatarService.loadAvatar(userId, size);
    }

    @GetMapping("/me")
    public ResponseEntity<byte[]> loadOwnerAvatar(@RequestHeader(value = "Authorization") String authHeader,
                                                  @RequestParam(required = false, defaultValue = "MEDIUM") SizeType size) {
        return avatarService.loadAvatar(authHeader, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadAvatar(@RequestHeader(value = "Authorization") String authHeader,
                             @RequestParam("file") MultipartFile file) {
        avatarService.saveAvatar(authHeader, file);
    }
}
