package ru.atott.combiq.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.atott.combiq.rest.bean.MarkdownContentBean;
import ru.atott.combiq.rest.bean.UploadFileBean;
import ru.atott.combiq.rest.request.MarkdownRequest;
import ru.atott.combiq.service.file.FileDescriptor;
import ru.atott.combiq.service.file.FileService;
import ru.atott.combiq.service.file.Location;
import ru.atott.combiq.service.markdown.MarkdownService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

@RestController
public class MarkdownRestController extends BaseRestController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MarkdownService markdownService;

    /**
     * Преобразовать markdown разметку в HTML.
     *
     * @request.body.example
     *      {@link ru.atott.combiq.rest.request.MarkdownRequest#EXAMPLE}
     *
     * @response.200.doc
     *      В случае успеха.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.MarkdownContentBean#EXAMPLE}
     */
    @RequestMapping(value = "/rest/v1/markdown", method = RequestMethod.POST)
    @ResponseBody
    public Object preview(@RequestBody MarkdownRequest request) {
        MarkdownContentBean response = new MarkdownContentBean();
        response.setMarkdown(request.getMarkdown());
        response.setHtml(markdownService.toHtml(getContext().getUc(), request.getMarkdown()));
        return response;
    }

    /**
     * Загрузить изображение, которое может использоваться в markdown разметке.
     *
     * @response.200.doc
     *      В случае успеха возврашает UploadFileBean с адресом изображения.
     *
     * @response.200.example
     *      {@link ru.atott.combiq.rest.bean.UploadFileBean#EXAMPLE}
     */
    @RequestMapping(value="/rest/v1/markdown/image", method=RequestMethod.POST)
    @ResponseBody
    public Object uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Location location = Location.allocateLocalLocation(filename);
        try (OutputStream outputStream = fileService.getOutputStream(location)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                bufferedOutputStream.write(bytes);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
        }
        return new UploadFileBean(location);
    }

    /**
     * Вернуть изображение, которое ранее было загружено с использованием метода POST /rest/v1/markdown/image.
     *
     * @response.200.doc
     *      Возвращает контент изображения в случае успеха.
     */
    @RequestMapping(value="/rest/v1/markdown/image/{location}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("location") String locationString) {
        Location location = Location.parse(locationString);

        try (FileDescriptor fileDescriptor = fileService.getFileDescriptor(location)) {

            return ResponseEntity.ok()
                    .contentLength(fileDescriptor.getSize())
                    .contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(location.getFilename())))
                    .body(new InputStreamResource(fileDescriptor.getInputStream()));
        }
    }
}
