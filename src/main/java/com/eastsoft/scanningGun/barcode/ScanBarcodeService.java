package com.eastsoft.scanningGun.barcode;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

/** 
 * 
 * 利用HOOK技术监听键盘事件（扫描枪就相当于只有0-9和回车键的键盘）
 * 低级键盘事件监听
 * @author tbl
 */
public class ScanBarcodeService {
    private HHOOK hhkKeyBoard;
    private final User32 lib = User32.INSTANCE;
    
    /**
     * 停止扫码枪服务
     */
    public void stopScanBarcodeService() {    
        //卸载键盘钩子
        lib.UnhookWindowsHookEx(hhkKeyBoard);    
    }

    /**
     * 启动扫码枪服务
     */
    public void startScanBarcodeService() {
        //键盘事件监听
        final BarcodeKeyboardListener listener=new BarcodeKeyboardListener();
        //System.out.println("++++++++++1");
        //回调
        LowLevelKeyboardProc keyboardHook = new LowLevelKeyboardProc() {

            @Override
            public LRESULT callback(int nCode, WPARAM wParam,
                    KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                	//System.out.println("++++++++++3");
                    switch (wParam.intValue()) {
                        case WinUser.WM_KEYUP:
                            int keyCode = info.vkCode;
                            char ch = (char) keyCode;
//                            System.out.print("keyCode="+keyCode);
                           
                            //监听数字键0-9
                            if (keyCode >= 48 && keyCode <= 58) {
                                //交个监听器处理
                            	//System.out.print(ch);
                                listener.onKey(keyCode);
                            }else if(keyCode >= 65&&keyCode <= 90){
                            	//交个监听器处理
                            	//System.out.print(ch);
                                listener.onKey(keyCode);
                            }else if(keyCode==10){
                            	//System.out.print(ch);
                            	listener.onKey(keyCode);
                            }else if (keyCode == 13) {
                                //交个监听器处理
                                listener.onKey(keyCode);
                            }
                            break;
                    }
                }
                //System.out.println("++++++++++2");
                //交个下一个钩子
                return lib.CallNextHookEx(hhkKeyBoard, nCode, wParam,
                        info.getPointer());
            }
        };
        
        System.out.println("监听结束");
        
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        
        //将上面定义的 回调方法 安装到挂钩链中对系统的底层的键盘输入事件进行监控
        hhkKeyBoard = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);
        
        // 处理消息（线程阻塞）
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                        System.err.println("error in get message");
                        break;
                } else {
                        lib.TranslateMessage(msg);
                        lib.DispatchMessage(msg);
                }
        }
    }
}
