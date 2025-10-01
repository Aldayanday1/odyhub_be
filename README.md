<div align="center">

# 🏛️
## **Odyhub Backend**
### *Sistem Pengaduan Masyarakat*

</div>

<div align="center">

![Java](https://img.shields.io/badge/Java-20-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Flutter](https://img.shields.io/badge/Flutter-Compatible-02569B?style=for-the-badge&logo=flutter&logoColor=white)

**Backend API yang powerful untuk aplikasi mobile Odyhub - Platform Pengaduan Masyarakat berbasis Flutter**

[Features](#-features) • [Tech Stack](#️-tech-stack) • [API Documentation](#-api-documentation) • [Installation](#-installation) • [Architecture](#-architecture)

</div>

## 📱 Tentang Odyhub

**Odyhub** adalah platform digital yang memungkinkan masyarakat untuk melaporkan berbagai permasalahan dan keluhan di lingkungan mereka secara cepat dan transparan. Backend ini menyediakan RESTful API yang robust untuk mendukung aplikasi mobile Flutter dengan fitur-fitur modern seperti:

- ✅ **Autentikasi Multi-Layer** dengan OTP Email & JWT Token
- 🔐 **Keamanan Tinggi** menggunakan Argon2 Password Hashing
- 📍 **Geolocation Support** untuk tracking lokasi pengaduan
- 📸 **Multi-Image Upload** dengan optimisasi storage
- 📊 **Real-time Status Tracking** untuk setiap laporan
- 📧 **Email Notification System** untuk verifikasi dan notifikasi
- 👥 **Role-Based Access Control** (User & Admin)
- 📈 **Analytics Dashboard** untuk monitoring pengaduan

---

## ✨ Features

### 🔐 Authentication & Authorization
- **User Registration** dengan verifikasi OTP via email
- **Secure Login** dengan Two-Factor Authentication (2FA)
- **JWT Token Management** dengan blacklist support
- **Admin Authentication** tanpa OTP untuk akses cepat
- **Session Management** dengan auto-expiry
- **Password Hashing** menggunakan Argon2 (state-of-the-art)

### 📋 Manajemen Pengaduan
- **Create Report** dengan lokasi GPS, foto, dan kategori
- **Update Report** untuk edit pengaduan yang dibuat
- **Delete Report** dengan validasi kepemilikan
- **View My Reports** untuk tracking pengaduan pribadi
- **Search & Filter** berdasarkan judul, kategori, dan status
- **Real-time Updates** untuk perubahan status laporan

### 🎯 Kategorisasi Laporan
Mendukung 10 kategori pengaduan:
- 🏗️ Infrastruktur (jalan rusak, lampu mati, dll)
- 🌳 Lingkungan (sampah, polusi, dll)
- 🚗 Transportasi (macet, parkir liar, dll)
- 🛡️ Keamanan (pencurian, kriminal, dll)
- 🏥 Kesehatan (fasilitas kesehatan, dll)
- 📚 Pendidikan (sekolah, fasilitas pendidikan, dll)
- 👥 Sosial (kemiskinan, kesejahteraan, dll)
- 📄 Izin & Perizinan
- 🏛️ Birokrasi & Pelayanan Publik
- 📌 Lainnya

### 👨‍💼 Panel Admin
- **Dashboard Analytics** dengan statistik harian
- **Status Management** untuk update progress laporan
- **Response System** untuk memberikan tanggapan
- **Filter by Status** (Pending, In Progress, Done)
- **Daily Report Chart** untuk monitoring trend

### 👤 User Profile Management
- **Profile Customization** dengan foto profil & background
- **Image Upload & Storage** yang teroptimasi
- **Profile Settings** untuk update informasi pribadi

### 📧 Email Service
- **OTP Email** dengan template HTML yang menarik
- **Registration Confirmation** dengan expiry time
- **Login Verification** untuk keamanan ekstra
- **Responsive Email Design** yang mobile-friendly

---

## 🛠️ Tech Stack

### Backend Framework
- **Spring Boot 3.2.3** - Modern Java framework
- **Spring Data JPA** - ORM untuk database operations
- **Spring Web** - RESTful API development
- **Spring Mail** - Email integration

### Security
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Argon2 Password Encoder** - Secure password hashing
- **Spring Security Crypto** - Cryptographic operations

### Database
- **MySQL 8.0** - Primary database
- **Hibernate** - JPA implementation
- **Connection Pooling** - Optimized database connections

### File Storage
- **Local File System** - Optimized file management
- **Multi-part File Upload** - Support large files up to 10MB
- **Image Processing** - Automatic optimization

### Email Service
- **JavaMail API** - Email functionality
- **SMTP Gmail Integration** - Reliable email delivery
- **HTML Email Templates** - Professional email design

### Tools & Utilities
- **Maven** - Dependency management
- **Jackson** - JSON processing
- **SLF4J & Logback** - Logging framework
- **JUnit** - Unit testing

---

## 📊 Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      MOBILE APP (Flutter)                   │
│                           Odyhub                            │
└────────────────────────────┬────────────────────────────────┘
                             │ 
                             │  HTTP/HTTPS
                             │  REST API
                             ▼ 
┌─────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND                      │
│                                                             │
│     ┌─────────────┐  ┌──────────────┐  ┌─────────────┐      │
│     │ Controllers │  │   Services   │  │ Repositories│      │
│     └─────────────┘  └──────────────┘  └─────────────┘      │
│            │                │                 │             │
│            │                │                 │             │
│            ▼                ▼                 ▼             │
│     ┌─────────────┐  ┌──────────────┐  ┌─────────────┐      │
│     │  JWT Auth   │  │ Email Service│  │   MySQL DB  │      │
│     │   Filter    │  │    (SMTP)    │  │    (JPA)    │      │
│     └─────────────┘  └──────────────┘  └─────────────┘      │
│                                                             │
│     ┌─────────────┐  ┌──────────────┐  ┌─────────────┐      │
│     │File Storage │  │  Scheduled   │  │    Util     │      │
│     │  Service    │  │    Tasks     │  │   Classes   │      │ 
│     └─────────────┘  └──────────────┘  └─────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### Database Schema

```sql
┌───────────────┐     ┌─────────────────┐     ┌──────────────────┐
│    User       │     │   Pengaduan     │     │  StatusLaporan   │
├────────────── ┤     ├─────────────────┤     ├──────────────────┤
│ id (PK)       │────<│ user_id (FK)    │>─── │ pengaduan_id (FK)│
│ nama          │     │ id (PK)         │     │ id (PK)          │
│ email         │     │ judul           │     │ status_sebelumnya│
│ password      │     │ alamat          │     │ status_baru      │
│ role          │     │ deskripsi       │     │ tanggapan        │
│ otp_code      │     │ kategori        │     │ gambar           │
│ otp_expiry    │     │ gambar          │     │ changed_at       │
│ is_registering│     │ latitude        │     └──────────────────┘
└───────────────┘     │ longitude       │
                      │ created_at      │     ┌──────────────────┐
                      │ updated_at      │     │  UserProfile     │
                      └─────────────────┘     ├──────────────────┤
                                              │ id (PK)          │
                                              │ user_id (FK)     │
                                              │ profile_image    │
                                              │ background_image │
                                              └──────────────────┘
```

---

## 🚀 Installation

### Prerequisites

- ☕ **Java JDK 17+** (Recommended: JDK 20)
- 🗄️ **MySQL 8.0+**
- 📦 **Maven 3.6+** (or use IDE built-in)
- 📧 **Gmail Account** (for SMTP email service)

### Step 1: Clone Repository

```bash
git clone https://github.com/Aldayanday1/odyhub_be.git
cd odyhub_be
```

### Step 2: Setup Database

1. **Start MySQL Service:**
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl start mysql
   ```

2. **Create Database:**
   ```sql
   mysql -u root -p
   CREATE DATABASE sistem_pengaduan;
   exit;
   ```

### Step 3: Configure Application

#### Option A: Menggunakan Environment Variables (RECOMMENDED)

1. **Copy template environment variables:**
   ```bash
   cp .env.example .env
   ```

2. **Edit file `.env` dengan credentials Anda:**
   ```properties
   # Database
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password
   
   # Email SMTP
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-gmail-app-password
   
   # JWT Secret (generate dengan: openssl rand -base64 32)
   SECRET_KEY=your-jwt-secret-key
   
   # Upload folder
   UPLOAD_FOLDER=./status-laporan-images
   ```

3. **Generate Gmail App Password:**
   - Aktifkan 2FA: https://myaccount.google.com/security
   - Generate App Password: https://myaccount.google.com/apppasswords
   - Pilih "Mail" dan copy 16-character password

4. **File `.env` sudah di-ignore oleh Git** - aman untuk menyimpan credentials

#### Option B: Set Environment Variables Manual

**Windows PowerShell:**
```powershell
$env:DB_PASSWORD="your_password"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:SECRET_KEY="your-secret-key"
```

**Linux/Mac:**
```bash
export DB_PASSWORD="your_password"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export SECRET_KEY="your-secret-key"
```

### Step 4: Create Upload Folders

```bash
# Create folders for image storage
mkdir uploads
mkdir status-laporan-images
```

### Step 5: Run Application

**Option 1: Using Maven**
```bash
mvn spring-boot:run
```

**Option 2: Using IDE**
- **NetBeans**: Right-click project → Run
- **IntelliJ IDEA**: Click Run button
- **Eclipse**: Right-click → Run As → Spring Boot App

**Option 3: Using JAR**
```bash
mvn clean package
java -jar target/sistem_pengaduan-0.0.1-SNAPSHOT.jar
```

### Step 6: Verify Installation

```bash
# Check if server is running
curl http://localhost:8080/api/users/all
```

✅ **Backend is ready!** Now you can connect your Flutter mobile app.

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/users
```

### Authentication Endpoints

#### 1. Register User
```http
POST /register
Content-Type: application/json

{
  "nama": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "message": "Registrasi berhasil. Silakan cek email Anda untuk kode OTP."
}
```

#### 2. Verify OTP (Registration)
```http
POST /verify-otp?otp=1234
```

**Response:**
```json
{
  "message": "Verifikasi OTP berhasil. Silakan login."
}
```

#### 3. Login User
```http
POST /login?email=john@example.com&password=SecurePass123
```

**Response:**
```json
{
  "message": "OTP telah dikirimkan ke email Anda."
}
```

#### 4. Verify OTP (Login)
```http
POST /login-with-otp
Content-Type: application/json

{
  "otp": "1234"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Login berhasil",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 5. Login Admin
```http
POST /admin-login
Content-Type: application/json

{
  "nama": "Admin",
  "password": "AdminPass123"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Login berhasil",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 6. Logout
```http
POST /logout
Authorization: Bearer <token>
```

---

### Pengaduan (Report) Endpoints

#### 1. Create Pengaduan
```http
POST /add
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
- judul: "Jalan Rusak di Depan Kantor"
- alamat: "Jl. Merdeka No. 123"
- deskripsi: "Jalan berlubang dan berbahaya"
- kategori: "INFRASTRUKTUR"
- latitude: -6.200000
- longitude: 106.816666
- gambar: [file]
```

**Response:**
```json
{
  "id": 1,
  "judul": "Jalan Rusak di Depan Kantor",
  "alamat": "Jl. Merdeka No. 123",
  "deskripsi": "Jalan berlubang dan berbahaya",
  "kategori": "INFRASTRUKTUR",
  "gambar": "http://192.168.56.1:8080/api/users/uploads/image.jpg",
  "latitude": -6.200000,
  "longitude": 106.816666,
  "status": "PENDING",
  "namaPembuat": "John Doe",
  "profileImagePembuat": "http://192.168.56.1:8080/images/profile.png",
  "createdAt": "2025-10-01T10:30:00",
  "updatedAt": "2025-10-01T10:30:00"
}
```

#### 2. Get All Pengaduan
```http
GET /all
```

#### 3. Get My Pengaduan
```http
GET /my-pengaduan
Authorization: Bearer <token>
```

#### 4. Get Pengaduan by ID
```http
GET /{id}
```

#### 5. Update Pengaduan
```http
PUT /update/{id}
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
- judul: "Updated Title"
- deskripsi: "Updated Description"
- gambar: [file] (optional)
```

#### 6. Delete Pengaduan
```http
DELETE /delete/{id}
Authorization: Bearer <token>
```

#### 7. Search Pengaduan
```http
GET /search?judul=jalan
```

#### 8. Filter by Category
```http
GET /kategori/INFRASTRUKTUR
GET /kategori/LINGKUNGAN
GET /kategori/TRANSPORTASI
```

---

### Status Laporan Endpoints (Admin Only)

#### 1. Update Status Laporan
```http
PUT /update-status/{pengaduanId}
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

Form Data:
- statusBaru: "PROGRESS" | "DONE"
- tanggapan: "Sedang ditangani oleh tim"
- gambar: [file] (optional)
```

**Response:**
```json
{
  "id": 1,
  "pengaduan": { ... },
  "statusSebelumnya": "PENDING",
  "statusBaru": "PROGRESS",
  "tanggapan": "Sedang ditangani oleh tim",
  "gambar": "http://192.168.56.1:8080/api/users/uploads/response.jpg",
  "changedAt": "2025-10-01T11:00:00"
}
```

#### 2. Get Pengaduan by Status (Admin)
```http
GET /pengaduan-by-status/PENDING
GET /pengaduan-by-status/PROGRESS
GET /pengaduan-by-status/DONE
Authorization: Bearer <admin-token>
```

#### 3. Get Daily Count (Admin Analytics)
```http
GET /daily-count
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "MONDAY": 15,
  "TUESDAY": 23,
  "WEDNESDAY": 18,
  "THURSDAY": 20,
  "FRIDAY": 25,
  "SATURDAY": 12,
  "SUNDAY": 8
}
```

---

### User Profile Endpoints

#### 1. Get Profile
```http
GET /profile
Authorization: Bearer <token>
```

**Response:**
```json
{
  "nama": "John Doe",
  "email": "john@example.com",
  "profileImage": "http://192.168.56.1:8080/images/profile.png",
  "backgroundImage": "http://192.168.56.1:8080/images/background.jpg"
}
```

#### 2. Update Profile
```http
PUT /profile/update
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
- profileImage: [file] (optional)
- backgroundImage: [file] (optional)
```

---

### File Access Endpoint

#### Get Uploaded Image
```http
GET /uploads/{filename}
```

Example:
```
http://localhost:8080/api/users/uploads/image.jpg
```

---

## 🔐 Security Features

### Password Security
- **Argon2 Algorithm** dengan parameter custom:
  - Memory: 16 MB
  - Iterations: 32
  - Parallelism: 1
  - Salt Length: 4096 bytes
  - Hash Length: 64 bytes

### JWT Token Security
- **Token Expiration**: 40 menit (2400 seconds)
- **Token Blacklist**: Logout akan masukkan token ke blacklist
- **Role-Based Claims**: Token menyimpan role (USER/ADMIN)
- **Secret Key**: Disimpan di environment variable

### OTP Security
- **Expiry Time**: 1 menit
- **Auto-Cleanup**: Scheduled task menghapus OTP expired
- **Single Use**: OTP dihapus setelah verifikasi berhasil
- **Random Generation**: 4-digit random number

### API Security
- **Authorization Header**: Bearer token untuk protected endpoints
- **Role Validation**: Middleware memeriksa role untuk admin endpoints
- **Token Validation**: Setiap request divalidasi JWT-nya
- **CORS Configuration**: Atur allowed origins sesuai kebutuhan

---

## 🎯 Flutter Integration Guide

### Setup HTTP Client (Flutter)

```dart
// lib/services/api_service.dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ApiService {
  static const String baseUrl = 'http://YOUR_IP:8080/api/users';
  static String? authToken;

  // Register User
  static Future<Map<String, dynamic>> register({
    required String nama,
    required String email,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'nama': nama,
        'email': email,
        'password': password,
      }),
    );
    return jsonDecode(response.body);
  }

  // Verify OTP
  static Future<String> verifyOtp(String otp) async {
    final response = await http.post(
      Uri.parse('$baseUrl/verify-otp?otp=$otp'),
    );
    return response.body;
  }

  // Login
  static Future<Map<String, dynamic>> login({
    required String email,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/login?email=$email&password=$password'),
    );
    return {'message': response.body};
  }

  // Login with OTP
  static Future<Map<String, dynamic>> loginWithOtp(String otp) async {
    final response = await http.post(
      Uri.parse('$baseUrl/login-with-otp'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'otp': otp}),
    );
    final data = jsonDecode(response.body);
    if (data['status'] == 'success') {
      authToken = data['token'];
    }
    return data;
  }

  // Create Pengaduan
  static Future<Map<String, dynamic>> createPengaduan({
    required String judul,
    required String alamat,
    required String deskripsi,
    required String kategori,
    required double latitude,
    required double longitude,
    required File gambar,
  }) async {
    var request = http.MultipartRequest(
      'POST',
      Uri.parse('$baseUrl/add'),
    );
    
    request.headers['Authorization'] = 'Bearer $authToken';
    request.fields['judul'] = judul;
    request.fields['alamat'] = alamat;
    request.fields['deskripsi'] = deskripsi;
    request.fields['kategori'] = kategori;
    request.fields['latitude'] = latitude.toString();
    request.fields['longitude'] = longitude.toString();
    request.files.add(await http.MultipartFile.fromPath('gambar', gambar.path));
    
    final response = await request.send();
    final responseData = await response.stream.bytesToString();
    return jsonDecode(responseData);
  }

  // Get All Pengaduan
  static Future<List<dynamic>> getAllPengaduan() async {
    final response = await http.get(Uri.parse('$baseUrl/all'));
    return jsonDecode(response.body);
  }

  // Get My Pengaduan
  static Future<List<dynamic>> getMyPengaduan() async {
    final response = await http.get(
      Uri.parse('$baseUrl/my-pengaduan'),
      headers: {'Authorization': 'Bearer $authToken'},
    );
    return jsonDecode(response.body);
  }
}
```

### Example Usage in Flutter Widget

```dart
// Example: Login Screen
class LoginScreen extends StatelessWidget {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void handleLogin() async {
    try {
      // Step 1: Request OTP
      await ApiService.login(
        email: emailController.text,
        password: passwordController.text,
      );
      
      // Step 2: Navigate to OTP screen
      Navigator.push(context, MaterialPageRoute(
        builder: (context) => OtpVerificationScreen(),
      ));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Login failed: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // Your UI implementation
  }
}
```

---

## 🗂️ Project Structure

```
sistem_pengaduan/
├── src/
│   ├── main/
│   │   ├── java/sistem_pengaduan/
│   │   │   ├── SistemPengaduanApplication.java
│   │   │   └── demo/
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── UserController.java
│   │   │       │   ├── PengaduanController.java
│   │   │       │   ├── StatusLaporanController.java
│   │   │       │   ├── UserProfileController.java
│   │   │       │   └── FileStorageService.java
│   │   │       ├── model/
│   │   │       │   ├── User.java
│   │   │       │   ├── UserRole.java
│   │   │       │   ├── UserProfile.java
│   │   │       │   ├── Pengaduan.java
│   │   │       │   ├── Kategori.java
│   │   │       │   ├── StatusLaporan.java
│   │   │       │   └── Status.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepo.java
│   │   │       │   ├── UserProfileRepo.java
│   │   │       │   ├── PengaduanRepo.java
│   │   │       │   └── StatusLaporanRepo.java
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   ├── PengaduanService.java
│   │   │       │   ├── StatusLaporanService.java
│   │   │       │   ├── EmailServiceRegist.java
│   │   │       │   ├── EmailServiceLogin.java
│   │   │       │   ├── FileStorageException.java
│   │   │       │   └── ScheduledTasks.java
│   │   │       └── util/
│   │   │           ├── JwtUtil.java
│   │   │           └── JwtRequestFilter.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── META-INF/
│   │           └── persistence.xml
│   └── test/
│       └── java/sistem_pengaduan/demo/
│           └── SistemPengaduanApplicationTests.java
├── uploads/                      # User uploaded images
├── status-laporan-images/        # Admin response images
├── pom.xml
├── README.md
├── EMAIL_SETUP_GUIDE.md
├── PORT_CONFLICT_GUIDE.md
└── start-app.bat
```

---

## 🔧 Configuration Files

### application.properties
```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/sistem_pengaduan?serverTimezone=Asia/Jakarta
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Upload Folders
upload.folder=./status-laporan-images
```

---

## 📈 Performance & Optimization

### Database Optimization
- ✅ **Connection Pooling** dengan HikariCP
- ✅ **Lazy Loading** untuk relasi OneToMany
- ✅ **Index Optimization** pada foreign keys
- ✅ **Query Optimization** dengan JPQL

### API Optimization
- ✅ **Compression** enabled untuk response
- ✅ **Lazy Initialization** untuk dependencies
- ✅ **Efficient JSON Serialization** dengan Jackson
- ✅ **Paginated Responses** untuk list endpoints

### Security Optimization
- ✅ **Argon2** lebih aman dari BCrypt
- ✅ **JWT Stateless** mengurangi database hits
- ✅ **Token Blacklist** in-memory untuk performa
- ✅ **Scheduled Cleanup** untuk expired OTP

---

## 💡 Troubleshooting

### Port Already in Use
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### MySQL Connection Failed
```bash
# Check MySQL service
net start MySQL80        # Windows
sudo systemctl start mysql  # Linux

# Verify connection
mysql -u root -p
```

### Email Authentication Failed
- Pastikan 2FA aktif di Gmail
- Generate App Password baru
- Update `application.properties` dengan App Password

### Out of Memory Error
```bash
# Set Java memory options
export MAVEN_OPTS="-Xms256m -Xmx1024m"
mvn spring-boot:run
```

---

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Test API with cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"nama":"Test User","email":"test@example.com","password":"Test123"}'
```

**Login:**
```bash
curl -X POST "http://localhost:8080/api/users/login?email=test@example.com&password=Test123"
```

### Test with Postman
Import collection dengan endpoint yang ada di [API Documentation](#-api-documentation).

---

## 🚀 Deployment

### Deploy to Production Server

1. **Build JAR:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Set Environment Variables:**
   ```bash
   export SECRET_KEY="production-secret-key"
   export MAIL_USERNAME="production-email@gmail.com"
   export MAIL_PASSWORD="production-app-password"
   export SERVER_PORT=8080
   ```

3. **Run Application:**
   ```bash
   java -jar target/sistem_pengaduan-0.0.1-SNAPSHOT.jar
   ```

4. **Setup as Service (Linux):**
   ```bash
   sudo nano /etc/systemd/system/odyhub-backend.service
   
   [Unit]
   Description=Odyhub Backend Service
   After=mysql.service
   
   [Service]
   User=youruser
   ExecStart=/usr/bin/java -jar /path/to/sistem_pengaduan.jar
   SuccessExitStatus=143
   
   [Install]
   WantedBy=multi-user.target
   
   sudo systemctl enable odyhub-backend
   sudo systemctl start odyhub-backend
   ```

---

## 📝 API Response Format

### Success Response
```json
{
  "status": "success",
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "timestamp": "2025-10-01T10:30:00"
}
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Developer

**Aldi Raihan**
- GitHub: [@Aldayanday1](https://github.com/Aldayanday1)
- Repository: [odyhub_be](https://github.com/Aldayanday1/odyhub_be)

---

## 🙏 Acknowledgments

- Spring Boot Team untuk framework yang luar biasa
- MySQL Team untuk database yang reliable
- Gmail SMTP untuk email service
- Flutter Community untuk mobile app integration

---

## 📞 Support

Jika ada pertanyaan atau issue:
- 💡 [Report Bug & Request Feature](https://github.com/Aldayanday1/odyhub_be/issues)
- 📧 Email: [onlymarfa69@gmail.com](mailto:onlymarfa69@gmail.com)

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ for better community engagement

</div>
