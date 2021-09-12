# DApps with Wallet Connect for Android

## Giới thiệu

Repo giới đây là thư viện hỗ trợ implement Wallet Connect cho Android kèm Demo

## Cấu trúc

Library gồm 2 file chính

- DAppsActivity: Implement từ AppCompatActivity, dùng để thay thế cho AppCompatActivity kèm với đó là 1 số hàm cần thiết như initialSetup và onMethodCall
- DApp: Là thành phần chính điều khiển DApps

## Hướng dẫn 

- Ở MainActivity thay vì implement mặc định theo AppCompatActivity, sử dụng **DAppsActivity** để thay thế. và override hàm sau

  ```kotlin
   override fun onStatus(status: Session.Status) {
          when (status) {
              Session.Status.Approved -> sessionApproved()
              Session.Status.Closed -> sessionClosed()
              Session.Status.Connected -> requestConnectionToWallet()
              Session.Status.Disconnected,
              is Session.Status.Error -> {
                  // TODO: Handle error
              }
          }
      }
  ```

Mặc định ở `DAppActivity` đã được override sẵn hàm `onStart` để khi khởi chạy, app sẽ thiết lập session mới

Các trạng thái của Session bao gồm

- Approved: Khi Wallet trên máy Approved 
- Closed: Khi Wallet trên máy Disconnect
- Connected: Khi session đã kết nối thành công để Bridge server (mặc định là WalletConnect của Trust Wallet)
- Disconnected: Khi session disconnect với Bridge server
- Error: Throw lỗi xảy ra

Ở đây có thể tham khảo `sessionApproved` và `sessionClosed` trong demo

Để lấy thông tin connect khi đã Connected sử dụng `DApp.config.toWCUri()` (Tham khảo `requestConnectionToWallet`). Sau khi sinh ra chuỗi có dạng `wc://...` có thể start activity với uri trên, mặc định tất cả các app có hỗ trợ WC trên Android dể sử dụng url schema như trên (theo docs của Wallet Connect)
