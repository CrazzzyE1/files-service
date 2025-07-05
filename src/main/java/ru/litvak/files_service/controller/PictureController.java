package ru.litvak.files_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litvak.files_service.service.PictureService;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureService pictureService;

    @GetMapping("/gift/{giftId}")
    public ResponseEntity<byte[]> getGiftPicture(@RequestHeader(value = "Authorization") String authHeader,
                                                 @PathVariable String giftId) {
        return pictureService.getGiftPicture(authHeader, giftId);
    }
}
