-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 02, 2021 at 06:29 AM
-- Server version: 10.4.20-MariaDB
-- PHP Version: 7.3.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `id17520261_cipher_chat`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat`
--

CREATE TABLE `chat` (
  `ucid` bigint(11) NOT NULL,
  `room_id` varchar(23) NOT NULL,
  `phone_number` int(10) NOT NULL,
  `message` varchar(100) NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `chat`
--

INSERT INTO `chat` (`ucid`, `room_id`, `phone_number`, `message`, `sent_at`) VALUES
(3, '61301a4151d12', 712345678, 'Hi. Welcome to Google Cloud Messaging where messages are sent via the cloud.', '2021-09-02 04:25:26'),
(4, '61301a4151d12', 723456789, 'Hi. I can\'t believe it. It\'s working better than expected.', '2021-09-02 04:27:04');

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE `rooms` (
  `urid` varchar(23) NOT NULL,
  `room_name` varchar(15) NOT NULL,
  `room_password` longtext NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`urid`, `room_name`, `room_password`, `created_at`) VALUES
('61301a4151d12', 'Geeks', 'legends!', '2021-09-02 00:26:41'),
('613051adb564f', 'Bazenga', 'legends!', '2021-09-02 04:23:09');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `phone_number` int(10) NOT NULL,
  `firstname` varchar(15) NOT NULL,
  `lastname` varchar(15) NOT NULL,
  `username` varchar(15) NOT NULL,
  `encrypted_password` longtext NOT NULL,
  `gcm_registration_id` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`phone_number`, `firstname`, `lastname`, `username`, `encrypted_password`, `gcm_registration_id`, `created_at`) VALUES
(712345678, 'John', 'Doe', 'johndoe', '$2y$10$jN2IsRnbwcfxXAax6.Tk/uTCi2XHSNaKttu0X4BfLawHEb/UhU0T6', 'AIzaSyAnquG7Rw7JM9Z5Hr8SCKSpoqTjcDz9ZBk', '2021-09-02 04:18:12'),
(723456789, 'Jane', 'Doe', 'janedoe', '$2y$10$zB94.0vvGOCNTrMXD1Y0AeDsm/0hSvH.WgoOr4yAaSXPpNQD8A0ZS', 'AIzaSyAnquG7Rw7JM9Z5Hr8SCKSpoqTjcDz9ZBk', '2021-09-02 04:20:56');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat`
--
ALTER TABLE `chat`
  ADD PRIMARY KEY (`ucid`),
  ADD KEY `phone_number` (`phone_number`),
  ADD KEY `room_id` (`room_id`);

--
-- Indexes for table `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`urid`),
  ADD UNIQUE KEY `room_name` (`room_name`) USING BTREE;

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`phone_number`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat`
--
ALTER TABLE `chat`
  MODIFY `ucid` bigint(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat`
--
ALTER TABLE `chat`
  ADD CONSTRAINT `chat_ibfk_1` FOREIGN KEY (`phone_number`) REFERENCES `users` (`phone_number`),
  ADD CONSTRAINT `chat_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`urid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
