package com.chaty.controller;

import com.chaty.api.lianke.LiankeConfig;
import com.chaty.common.BaseResponse;
import com.chaty.dto.PrinterPropsDTO;
import com.chaty.dto.RemoteFileDTO;
import com.chaty.service.PrinterService;
import com.chaty.service.RemoteFileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/api/remote/file")
@RestController
public class RemoteFileController {

    @Resource
    private  RemoteFileService remoteFileService;

    @Resource
    private PrinterService printerService;

    @GetMapping("/allList")
    public BaseResponse<?> allListFiles(String path) {
        List<RemoteFileDTO> res = remoteFileService.list(path, "");
        return BaseResponse.ok(res);
    }
    
    @GetMapping("/list")
    public BaseResponse<?> listFiles(String path) {
        List<RemoteFileDTO> res = remoteFileService.list(path, "in");
        return BaseResponse.ok(res);
    }

    /**
     * 用于前端选择文件list来远程打印
     * @param path
     * @return
     */
    @GetMapping("/printList")
    public BaseResponse<?> printListFiles(String path) {
        List<RemoteFileDTO> res = remoteFileService.list(path, "out");
        return BaseResponse.ok(res);
    }

    /**
     * 用于前端选择文件来执行远程打印
     * @param printerPropsDTO
     * @return
     */
    @PostMapping("/print")
    public BaseResponse<?> print(@RequestBody PrinterPropsDTO printerPropsDTO) {
        printerService.printByLink(printerPropsDTO);
        return BaseResponse.ok("打印成功");
    }

    @GetMapping("/getPrintDevices")
    public BaseResponse<?> getPrintDevices() {
        return BaseResponse.ok(printerService.getDevices());
    }

    @GetMapping("/refreshPinterDevices")
    public BaseResponse<?> refreshPinterDevices() {
        List<LiankeConfig.Device> devices = printerService.refreshPinterDevices();
        return BaseResponse.ok("刷新成功", devices);
    }

    @GetMapping("/changePinterDevicesName")
    public BaseResponse<?> changePinterDevicesName(@RequestParam String deviceId, @RequestParam String newName) {
        printerService.changePinterDevicesName(deviceId, newName);
        return BaseResponse.ok("修改成功");
    }

    @GetMapping("/refreshPinterParams")
    public BaseResponse<?> refreshPinterParams(@RequestParam String deviceId) {
        printerService.refreshPinterConfig(deviceId);
        return BaseResponse.ok("修改成功");
    }
}
