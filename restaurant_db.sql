-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 27, 2025 lúc 09:14 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `restaurant_db`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chefs`
--

CREATE TABLE `chefs` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `experience` text NOT NULL,
  `image_path` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chefs`
--

INSERT INTO `chefs` (`id`, `name`, `experience`, `image_path`) VALUES
(3, 'Christine Hà', 'Là quán quân Vua đầu bếp MasterChef Mỹ 2012, nổi tiếng với khả năng nấu ăn xuất sắc dù bị khiếm thị. Tham gia nhiều chương trình ẩm thực lớn trên thế giới và là tác giả sách nấu ăn bán chạy.', '/resources/images/ChristineHa.jpg'),
(4, 'Luke Nguyễn', 'Đầu bếp nổi tiếng người Úc gốc Việt, chủ nhà hàng ở Sydney và nhiều chương trình truyền hình về ẩm thực du lịch. Chuyên về món ăn Việt Nam truyền thống và hiện đại.', '/resources/images/LukeNguyen.jpg'),
(5, 'Michael Bảo Huỳnh', 'Đầu bếp người Mỹ gốc Việt, từng làm việc tại nhiều nhà hàng Michelin Star và được biết đến với phong cách nấu ăn sáng tạo, kết hợp ẩm thực Á-Âu.', '/resources/images/MichaelBaoHuynh.jpg');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `dishes`
--

CREATE TABLE `dishes` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `price` double NOT NULL,
  `category` varchar(50) NOT NULL,
  `image_path` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `dishes`
--

INSERT INTO `dishes` (`id`, `name`, `description`, `price`, `category`, `image_path`) VALUES
(1, 'Bánh Mì Bơ Tỏi', 'Bánh mì nướng giòn rụm với hương vị bơ tỏi thơm lừng.', 65000, 'appetizer', '/resources/images/th1.jpg'),
(2, 'Salad Trái Cây', 'Salad tươi mát với các loại trái cây theo mùa và sốt đặc biệt.', 95000, 'appetizer', '/resources/images/th2.jpg'),
(3, 'Súp Bí Đỏ', 'Súp bí đỏ sánh mịn, béo ngậy, tốt cho sức khỏe.', 80000, 'appetizer', '/resources/images/th3.jpg'),
(4, 'Khoai Tây Chiên Phô Mai', 'Khoai tây chiên giòn tan phủ phô mai béo ngậy.', 75000, 'appetizer', '/resources/images/th4.jpg'),
(5, 'Bò Bít Tết Sốt Tiêu', 'Thịt bò thăn hảo hạng, nướng vừa tới, dùng kèm sốt tiêu xanh đậm đà.', 280000, 'main_course', '/resources/images/th5.jpg'),
(6, 'Mì Ý Sốt Kem Nấm', 'Sợi mì Ý dai ngon hòa quyện cùng sốt kem nấm truffle thơm lừng.', 160000, 'main_course', '/resources/images/th6.jpg'),
(7, 'Gà Nướng Mật Ong', 'Gà nướng nguyên con tẩm ướp mật ong, da giòn, thịt mềm.', 250000, 'main_course', '/resources/images/th7.jpg'),
(8, 'Pizza Hải Sản Cao Cấp', 'Đế bánh giòn tan, phủ đầy tôm, mực, nghêu tươi ngon và phô mai.', 190000, 'main_course', '/resources/images/th8.jpg'),
(9, 'Cá Hồi Áp Chảo', 'Cá hồi phi lê áp chảo vàng ruộm, giữ trọn vị ngọt tự nhiên.', 220000, 'main_course', '/resources/images/th9.jpg'),
(10, 'Sườn Nướng BBQ', 'Sườn non được ướp kỹ và nướng chậm cho đến khi mềm rục, đậm vị.', 270000, 'main_course', '/resources/images/th10.jpg'),
(11, 'Bánh Tiramisu', 'Bánh tiramisu truyền thống với hương cà phê và kem Mascarpone béo ngậy.', 90000, 'dessert', '/resources/images/th11.jpg'),
(12, 'Bánh Crepe Sầu Riêng', 'Bánh crepe mềm mại với nhân sầu riêng tươi thơm lừng.', 85000, 'dessert', '/resources/images/th12.jpg'),
(13, 'Kem Các Vị', 'Các vị kem homemade đặc biệt, tươi mát.', 70000, 'dessert', '/resources/images/th13.jpg'),
(14, 'Nước Ép Dưa Hấu', 'Nước ép dưa hấu tươi mát, giải khát tức thì.', 45000, 'beverage', '/resources/images/th14.jpg'),
(15, 'Mojito Chanh Bạc Hà', 'Thức uống cocktail không cồn, thanh mát và sảng khoái.', 60000, 'beverage', '/resources/images/th15.jpg'),
(16, 'Cà Phê Sữa Đá', 'Cà phê pha phin đậm đà kết hợp sữa đặc.', 50000, 'beverage', '/resources/images/th16.jpg'),
(17, 'Sinh Tố Bơ', 'Sinh tố bơ sánh mịn, bổ dưỡng.', 55000, 'beverage', '/resources/images/4.jpg'),
(18, 'Trà Sữa Trân Châu', 'Trà sữa thơm ngon với trân châu dai giòn.', 50000, 'beverage', '/resources/images/5.jpg'),
(19, 'Nước Ngọt Coca Cola', 'Nước giải khát có ga, sảng khoái.', 30000, 'beverage', '/resources/images/6.jpg'),
(20, 'Bia Tiger', 'Bia lạnh sảng khoái.', 40000, 'beverage', '/resources/images/7.jpg'),
(21, 'Rượu Vang Đỏ', 'Ly rượu vang đỏ thượng hạng.', 120000, 'beverage', '/resources/images/8.jpg'),
(22, 'Nước Khoáng Lavie', 'Nước khoáng tinh khiết.', 20000, 'beverage', '/resources/images/9.jpg'),
(23, 'Cocktail Blue Lagoon', 'Thức uống đẹp mắt và hấp dẫn.', 90000, 'beverage', '/resources/images/10.jpg'),
(24, 'Trà Chanh', 'Thức uống giải khát quen thuộc.', 35000, 'beverage', '/resources/images/11.jpg'),
(25, 'Sinh Tố Xoài', 'Sinh tố xoài tươi ngon.', 55000, 'beverage', '/resources/images/12.jpg'),
(26, 'Soda Blue Ocean', 'Thức uống soda mát lạnh với màu xanh đại dương.', 50000, 'Beverage', '/resources/images/14.jpg'),
(27, 'Espresso', 'Cà phê Espresso đậm đặc.', 40000, 'Beverage', '/resources/images/15.jpg');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `customer_name` varchar(255) NOT NULL,
  `customer_email` varchar(255) NOT NULL,
  `customer_phone` varchar(255) NOT NULL,
  `reservation_date` date NOT NULL,
  `reservation_time` time NOT NULL,
  `number_of_guests` int(11) NOT NULL,
  `special_requests` text NOT NULL,
  `status` varchar(50) DEFAULT 'confirmed',
  `total_price` double DEFAULT 0,
  `payment_status` varchar(50) DEFAULT 'unpaid',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `table_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `reservations`
--

INSERT INTO `reservations` (`id`, `customer_name`, `customer_email`, `customer_phone`, `reservation_date`, `reservation_time`, `number_of_guests`, `special_requests`, `status`, `total_price`, `payment_status`, `created_at`, `table_id`) VALUES
(1, 'nam', 'nam@gmail.com', '0944082389', '2025-06-09', '18:30:00', 2, 'tôi muốn tạo một bất ngờ', 'confirmed', 0, 'paid', '2025-05-29 15:41:34', 1),
(2, 'fd', 'ds@gmail.com', '00000', '2025-12-09', '18:30:00', 4, '.....', 'confirmed', 0, 'paid', '2025-06-03 03:11:54', 1),
(3, 'nam', 'nam@gmail.com', '0944082389', '2025-06-27', '19:00:00', 4, 'Món đã đặt:\n- Bánh Mì Bơ Tỏi x 1\n- Súp Bí Đỏ x 1\n- Salad Trái Cây x 1\n- Khoai Tây Chiên Phô Mai x 1\n', 'confirmed', 315000, 'paid', '2025-06-27 07:10:11', 1),
(4, 'nma', '132@gmail.com', '0366464', '2025-06-27', '19:00:00', 2, 'Món đã đặt:\n- Khoai Tây Chiên Phô Mai x 1\n', 'cancelled', 75000, 'unpaid', '2025-06-27 07:13:44', 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tables`
--

CREATE TABLE `tables` (
  `id` int(11) NOT NULL,
  `table_number` varchar(50) NOT NULL,
  `capacity` int(11) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `tables`
--

INSERT INTO `tables` (`id`, `table_number`, `capacity`, `location`, `status`) VALUES
(1, 'OUT-01', 4, 'outdoor', 'available'),
(2, 'OUT-02', 4, 'outdoor', 'available'),
(3, 'OUT-03', 4, 'outdoor', 'available'),
(4, 'OUT-04', 4, 'outdoor', 'available'),
(5, 'OUT-05', 4, 'outdoor', 'available'),
(6, 'IN-01', 6, 'indoor', 'available'),
(7, 'IN-02', 6, 'indoor', 'available'),
(8, 'IN-03', 6, 'indoor', 'available'),
(9, 'IN-04', 6, 'indoor', 'available'),
(10, 'IN-05', 6, 'indoor', 'available'),
(11, 'IN-06', 6, 'indoor', 'available'),
(12, 'IN-07', 6, 'indoor', 'available'),
(13, 'IN-08', 6, 'indoor', 'available'),
(14, 'IN-09', 6, 'indoor', 'available'),
(15, 'IN-10', 6, 'indoor', 'available');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role` varchar(50) DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone_number`, `role`) VALUES
(1, 'admin', 'admin123', NULL, NULL, 'admin'),
(2, 'user', 'user123', NULL, NULL, 'user'),
(3, 'nam', '123456', 'nam@gmail.com', '0944082389', 'user');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `chefs`
--
ALTER TABLE `chefs`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `dishes`
--
ALTER TABLE `dishes`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `tables`
--
ALTER TABLE `tables`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `table_number` (`table_number`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `chefs`
--
ALTER TABLE `chefs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `dishes`
--
ALTER TABLE `dishes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT cho bảng `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `tables`
--
ALTER TABLE `tables`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
