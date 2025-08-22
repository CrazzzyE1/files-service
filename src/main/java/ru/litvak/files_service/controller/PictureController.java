package ru.litvak.files_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.model.request.ClonePictureRequest;
import ru.litvak.files_service.service.PictureService;

import static ru.litvak.files_service.util.FileValidatorUtil.validateContentType;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureService pictureService;

    @GetMapping("/gift/{giftId}")
    public ResponseEntity<byte[]> getGiftPicture(@RequestHeader(value = "Authorization") String authHeader,
                                                 @PathVariable String giftId,
                                                 @RequestParam(required = false, defaultValue = "MEDIUM") SizeType size) {
        return pictureService.getGiftPicture(authHeader, giftId, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addPicture(@RequestHeader(value = "Authorization") String authHeader,
                           @RequestParam("giftId") String giftId,
                           @RequestParam("file") MultipartFile file) {
        validateContentType(file.getContentType());
        pictureService.addPicture(authHeader, giftId, file);
    }

    @PostMapping("/clone")
    public void clone(@RequestBody ClonePictureRequest request) {
        pictureService.clonePicture(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/gift/{giftId}")
    public void deletePicture(@RequestHeader(value = "Authorization") String authHeader, @PathVariable String giftId) {
        pictureService.deletePicture(authHeader, giftId);
    }
}
