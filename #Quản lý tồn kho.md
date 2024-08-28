# Quản lý tồn kho

  * Dùng lại màn hình danh sách sp
  * Thêm filter trạng thái "đã bán" (dựa trên các sản phẩm có ngày xuất kho)
  * Xuất excel với thông tin như file import (thêm trạng thái dựa vào ngày bán)
  
# Quản lý đơn hàng
   - Xuất pdf từng đơn hàng.
  * Tạo đơn bán hàng:
     - Mã đơn hàng (tự tạo với prefix PTS+DDMMYY+Random 4 chữ cái và số)
    - Nhập: tên, sdt, địa chỉ người mua
    - Người bán
    - Cửa hàng bán
    - Ngày giao
    - Hình thức thanh toán (COD, CK, Card, tiền mặt)
    - Số tiền đã thanh toán (cọc)
    - Giá giảm cho khách sỉ/ chạy sale ?
    - Chọn sản phẩm cần bán (search bằng serial hay nhập tên máy)
      - fill data sản phẩm
      - thông kho, serial, giá sản phẩm
      - Nhập số lượng 
    - Tính tiền đơn hàng dựa giá sp * số lượng - số tiền đã thanh toán - giá giảm
    - Ghi chú
    - Trạng thái giao hàng
    - Ngày đặt hàng
  * Cập nhật đơn hàng:
    - Thay đổi trạng thái đơn hàng (đóng gói, đã ship, đã giao, hủy, khách k nhận) và hiển ngày thay đổi từng trạng thái. Khi trạng thái chuyển sang đã giao -> Cập nhật sang đã bán
    - Ghi chú
  * Danh sách đơn hàng
     - Hiển thị mã đơn, ngày tạo, tên khách, trạng thái
     - cho filter bằng mã, ngày tạo, tên khách, trạng thái
  
   - Lưu danh thông tin người mua để gợi ý (tên, sdt, địa chỉ)
    
# Báo cáo nhập hàng
   ## Tạo đơn nhập hàng
  
    - Mã đơn hàng (tự tạo với prefix PTI+DDMMYY+Random 4 chữ cái và số)
    - Nhập thông tin nhà cung cấp tên, địa chỉ, sdt
    - Ngày nhập
    - Nhập thông tin cơ bản sản phẩm màu, màn, ram, ssd, cpu (có thể dùng gợi ý với sản phẩm đã từng nhập)
    - Nhập số serial
    - Giá nhập cho sp
    - Tổng tiền nhập hàng dựa vào số lượng sp * giá sp
    - Phương thức thanh toán (tiền mặt, ck )
    - Số tiền thanh toán
    - Số tiền còn nợ ?
  
  ## Chỉnh sửa đơn nhập hàng
  
    - Số tiền đã thanh toán, số tiền còn nợ ?
    - Hoàn trả đơn nhập
    
  ## Quản lý nhập hàng
  
    - Danh sách đơn nhập 
      - Mã đơn nhập, nhà cung cấp, ngày nhập, số tiền nhập, số tiền nợ, số tiền thanh toán
    - Cho filter theo nhà cung cấp, ngày nhập đơn nhập
    
# Báo cáo thu chi

  - Tạo chart hiển thị thu chi dựa trên số tiền bán hàng ra trong tháng - số tiền nhập hàng.
  - Tạo chart lợi nhuận dựa trên giá nhập hàng * số lượng hàng bán ra ? 
# Lịch sửa kho hàng cho sản phẩm