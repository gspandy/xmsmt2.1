package net.wit.controller.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.service.FileService;
import net.wit.util.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller("adminFileController")
@RequestMapping({"/admin/file"})
public class FileController extends BaseController
{

  @Resource(name="fileServiceImpl")
  private FileService fileService;

  @RequestMapping(value={"/upload"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"text/html; charset=UTF-8"})
  public void upload(FileInfo.FileType fileType, MultipartFile file, HttpServletResponse response)
  {
    Map data = new HashMap();
    if (!(this.fileService.isValid(fileType, file))) {
      data.put("message", Message.warn("admin.upload.invalid", new Object[0]));
    } else {
      String url = this.fileService.upload(fileType, file, false);
      if (url == null) {
        data.put("message", Message.warn("admin.upload.error", new Object[0]));
      } else {
        data.put("message", SUCCESS_MESSAGE);
        data.put("url", url);
      }
    }
    try {
      response.setContentType("text/html; charset=UTF-8");
      JsonUtils.writeValue(response.getWriter(), data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value={"/browser"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public List<FileInfo> browser(String path, FileInfo.FileType fileType, FileInfo.OrderType orderType)
  {
    return this.fileService.browser(path, fileType, orderType);
  }
}