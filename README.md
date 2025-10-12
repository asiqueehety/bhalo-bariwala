# Bhalo Bariwala 🏢

An Android application designed to streamline communication and management between tenants and landlords. Built with Java, this app provides a comprehensive platform for property management, complaint tracking, and real-time messaging.

[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Security](#security)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

**Bhalo Bariwala** (Bengali for "Good Landlord") is a mobile application that bridges the gap between property owners and tenants. The app facilitates seamless property management, complaint resolution, and direct communication channels, making rental management more efficient and transparent.

## ✨ Features

### 🔐 Authentication System
- **Dual Role Support**: Separate authentication flows for landlords and tenants
- **Secure Password Storage**: Implements salted password hashing using industry-standard security practices
- **Email Validation**: Prevents duplicate registrations and ensures unique user accounts

### 👤 For Landlords

#### Property Management
- **Add Multiple Properties**: Manage unlimited properties under a single account
- **Apartment Tracking**: Create and track individual apartments within each property
- **Rent Management**: Set and monitor rent amounts for each apartment

#### Tenant Management
- **Tenant Directory**: View all tenants across all properties
- **Contact Information**: Quick access to tenant contact details
- **Occupancy Overview**: Track which apartments are occupied

#### Complaint Management
- **Centralized Dashboard**: View all complaints from tenants across properties
- **Categorized Complaints**: Filter by type (Electricity, Gas, Water, Security, Maintenance)
- **Property & Apartment Association**: Each complaint is linked to specific property and apartment
- **Efficient Resolution**: Track and respond to maintenance requests promptly

#### Communication
- **Direct Messaging**: Real-time chat with tenants
- **Message History**: Persistent conversation records
- **Read Receipts**: Track message delivery status

### 🏠 For Tenants

#### Profile Management
- **Personal Information**: Update contact details and profile information
- **Property Association**: Link account to specific building and apartment

#### Complaint System
- **Submit Complaints**: Report issues in multiple categories:
  - 🔌 Electricity
  - 🔥 Gas
  - 💧 Water
  - 🔒 Security
  - 🔧 Maintenance
- **Complaint Tracking**: Monitor status and history of submitted complaints
- **Detailed Descriptions**: Provide comprehensive information for faster resolution

#### Directory Access
- **Tenant Directory**: View other tenants in the same property
- **Community Connection**: Access contact information of fellow residents

#### Communication
- **Message Landlord**: Direct communication channel with property owner
- **Real-time Chat**: Instant messaging for urgent matters
- **Message History**: Access previous conversations

## 🏗 Architecture

### Technology Stack
- **Language**: Java
- **Platform**: Android (Material Design Components)
- **Database**: SQLite (Local Database)
- **Design Pattern**: DAO (Data Access Object) Pattern
- **UI Framework**: Material Components for Android

### Key Components

#### Data Access Layer (DAO)
- `LandlordDAO`: Manages landlord authentication and data
- `TenantDAO`: Handles tenant operations and authentication
- `PropertyDAO`: Property CRUD operations
- `ApartmentDAO`: Apartment management
- `ComplaintDAO`: Complaint tracking and management
- `MessageDAO`: Chat message persistence

#### Security Layer
- `PasswordUtils`: Cryptographic utilities for secure password handling
  - Salt generation
  - Password hashing
  - Hash verification

#### UI Layer
- **Activities**: Screen controllers for different app flows
- **Adapters**: RecyclerView adapters for list displays
- **Models**: Data classes representing business entities

## 💾 Database Schema

### Tables

#### `landlord`
```sql
- l_id (PRIMARY KEY, AUTOINCREMENT)
- l_name (TEXT)
- l_email (TEXT, UNIQUE)
- l_contact (TEXT)
- l_password_hash (TEXT)
- l_salt (TEXT)
- l_created (INTEGER - timestamp)
```

#### `tenant`
```sql
- t_id (PRIMARY KEY, AUTOINCREMENT)
- t_name (TEXT)
- t_email (TEXT, UNIQUE)
- t_contact (TEXT)
- t_password_hash (TEXT)
- t_salt (TEXT)
- t_created (INTEGER - timestamp)
- t_prop_id (INTEGER, FOREIGN KEY -> property.prop_id)
- t_apt_id (INTEGER, FOREIGN KEY -> apartment.apt_id)
- t_lid (INTEGER, FOREIGN KEY -> landlord.l_id)
```

#### `property`
```sql
- prop_id (PRIMARY KEY, AUTOINCREMENT)
- prop_name (TEXT)
- landlord_id (INTEGER, FOREIGN KEY -> landlord.l_id)
```

#### `apartment`
```sql
- apt_id (PRIMARY KEY, AUTOINCREMENT)
- a_prop_id (INTEGER, FOREIGN KEY -> property.prop_id)
- a_rent (REAL)
```

#### `complaints`
```sql
- c_id (PRIMARY KEY, AUTOINCREMENT)
- c_title (TEXT)
- c_desc (TEXT)
- c_tid (INTEGER, FOREIGN KEY -> tenant.t_id)
- c_type (TEXT) -- electricity, gas, water, security, maintenance
```

#### `messages`
```sql
- m_id (PRIMARY KEY, AUTOINCREMENT)
- m_landlord_id (INTEGER, FOREIGN KEY -> landlord.l_id)
- m_tenant_id (INTEGER, FOREIGN KEY -> tenant.t_id)
- m_sender_type (TEXT) -- 'landlord' or 'tenant'
- m_message (TEXT)
- m_timestamp (INTEGER)
- m_is_read (INTEGER) -- boolean 0/1
```

## 🔒 Security

### Password Security
- **Salted Hashing**: Each password is hashed with a unique salt
- **No Plain Text Storage**: Passwords are never stored in readable format
- **Secure Validation**: Password verification uses constant-time comparison

### Data Protection
- **Local Storage**: All data stored locally on device using SQLite
- **Input Validation**: Comprehensive validation on user inputs
- **SQL Injection Prevention**: Parameterized queries throughout

## 📁 Project Structure

```
bhalo-bariwala/
├── app/
│   └── src/
│       └── main/
│           └── java/com/example/bhalobariwala/
│               ├── MainActivity.java
│               ├── SignUpActivity.java
│               ├── DatabaseHelper.java
│               ├── LandlordDAO.java
│               ├── TenantDAO.java
│               ├── PropertyDAO.java
│               ├── ApartmentDAO.java
│               ├── model/
│               │   ├── Apartment.java
│               │   ├── Property.java
│               │   └── Message.java
│               ├── security/
│               │   └── PasswordUtils.java
│               ├── ui/
│               │   ├── login/
│               │   │   └── LoginActivity.java
│               │   ├── tenant/
│               │   │   ├── TenantDashboardActivity.java
│               │   │   ├── TenantProfileActivity.java
│               │   │   ├── TenantComplaintsActivity.java
│               │   │   ├── TenantDirectoryActivity.java
│               │   │   └── TenantInfo.java
│               │   ├── owner/
│               │   │   ├── Complaint.java
│               │   │   └── ComplaintAdapter.java
│               │   └── chat/
│               │       └── TenantChatActivity.java
│               └── ...
└── README.md
```

## 🚀 Installation

### Prerequisites
- Android Studio (Arctic Fox or later)
- JDK 8 or higher
- Android SDK (API Level 21 or higher)
- Gradle 7.0+

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/asiqueehety/bhalo-bariwala.git
   cd bhalo-bariwala
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on emulator or device**
   - Connect an Android device via USB (with USB debugging enabled)
   - Or start an Android emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 📱 Usage

### First Launch

1. **Welcome Screen**: Choose between Login or Sign Up
2. **Sign Up**: 
   - Select role (Landlord or Tenant)
   - Landlords: Provide basic information
   - Tenants: Select building and apartment from available properties
3. **Login**: Use registered email and password

### For Landlords

1. **Add Property**: Navigate to Properties → Add New Property
2. **Add Apartments**: Select a property → Add Apartments
3. **View Complaints**: Dashboard → Complaints to see all tenant issues
4. **Message Tenants**: Dashboard → Messages → Select tenant
5. **View Tenant Directory**: Dashboard → Tenants to see all residents

### For Tenants

1. **Submit Complaint**: Dashboard → Complaints → New Complaint
2. **Message Landlord**: Dashboard → Message Landlord
3. **View Directory**: Dashboard → Directory to see neighbors
4. **Update Profile**: Dashboard → Profile

## 🤝 Contributing

We welcome contributions to Bhalo Bariwala! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Java naming conventions
- Write meaningful commit messages
- Add comments for complex logic
- Test thoroughly before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email [your-email] or open an issue in the repository.

## 🙏 Acknowledgments

- Material Design Components for Android
- Android SQLite Database
- The open-source community

---

**Made with ❤️ for better tenant-landlord relationships**
