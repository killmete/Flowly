# 💸 Flowly — Smart Daily Budget Predictor

> แอปพลิเคชัน Android สำหรับพยากรณ์งบประมาณรายวันแบบเรียลไทม์ ช่วยให้คุณวางแผนการใช้จ่ายอย่างชาญฉลาดตลอดทั้งเดือน

Flowly เป็นแอปจัดการการเงินส่วนบุคคลที่ใช้ **Prediction Engine** ในการคำนวณงบประมาณที่ปลอดภัยต่อวัน โดยวิเคราะห์จากรายรับ, ค่าใช้จ่ายคงที่, และพฤติกรรมการใช้จ่ายของผู้ใช้ เพื่อให้ทราบว่าเดือนนี้จะเหลือเงินเท่าไหร่ และควรใช้จ่ายวันละเท่าไหร่จึงจะพอถึงสิ้นเดือน

---

## 📱 Screenshots

| Dashboard | Add Expense | Timeline | Settings |
|:---------:|:-----------:|:--------:|:--------:|
| หน้าสรุปภาพรวมการเงิน | เพิ่มรายจ่ายพร้อมหมวดหมู่ | ดูประวัติรายจ่ายรายวัน | ตั้งค่ารายรับและค่าใช้จ่ายคงที่ |

---

## ✨ Features

### 🏠 Dashboard — ภาพรวมการเงิน
- **Prediction Card** — แสดงยอดเงินที่คาดว่าจะเหลือเมื่อสิ้นเดือน พร้อมสถานะ (🟢 Safe / 🟡 Warning / 🔴 Danger)
- **Budget Progress Bar** — วงกลมแสดงสัดส่วนการใช้จ่ายเทียบกับงบที่ใช้ได้
- **Safe Daily Budget** — งบที่ปลอดภัยต่อวัน คำนวณจากเงินคงเหลือหารด้วยจำนวนวันที่เหลือ
- **Spending Chart** — กราฟแนวโน้มการใช้จ่ายแบบ Gradient
- **Today's Expenses** — รายการค่าใช้จ่ายวันนี้พร้อมลบได้ด้วย Swipe

### ➕ Add Expense — เพิ่มรายจ่าย
- บันทึกจำนวนเงิน, หมวดหมู่, และหมายเหตุ
- รองรับ 6 หมวดหมู่: 🍔 Food, 🚗 Transport, 🛍️ Shopping, 🎬 Entertainment, 📄 Bills, 📦 Other
- แต่ละหมวดมีไอคอนและสีเฉพาะตัว

### 📅 Timeline — ประวัติรายจ่าย
- แสดงรายจ่ายแบบ Day-by-Day ย้อนหลังตลอดทั้งเดือน
- สรุปยอดรวมแต่ละวัน
- ดูรายละเอียดรายการในแต่ละวันได้

### ⚙️ Settings — ตั้งค่างบประมาณ
- กำหนดรายรับรายเดือน (Monthly Income / Allowance)
- ระบุค่าใช้จ่ายคงที่: ค่าโทรศัพท์, อินเทอร์เน็ต, Subscriptions, ค่าใช้จ่ายจำเป็นรายวัน
- คำนวณ **Usable Budget** (รายรับ − ค่าใช้จ่ายคงที่) แบบ Real-time
- บันทึกลง Room Database ต่อเดือน

### 🔮 Prediction Engine
- คำนวณค่าเฉลี่ยการใช้จ่ายต่อวัน
- พยากรณ์ค่าใช้จ่ายรวมทั้งเดือน
- คำนวณยอดคงเหลือเมื่อสิ้นเดือน
- กำหนดสถานะการใช้จ่าย:
  - 🟢 **SAFE** — คาดว่าจะเหลือ ≥ 20% ของงบ
  - 🟡 **WARNING** — คาดว่าจะเหลือ 0–20% ของงบ
  - 🔴 **DANGER** — คาดว่าจะใช้เกินงบ

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Navigation** | Jetpack Navigation Compose |
| **Local Database** | Room (SQLite) |
| **Annotation Processing** | KSP (Kotlin Symbol Processing) |
| **Async / Reactive** | Kotlin Coroutines + Flow |
| **State Management** | StateFlow + collectAsState |
| **Build System** | Gradle (Kotlin DSL) + Version Catalog |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 |

---

## 🗂️ Project Structure

```
app/src/main/java/com/example/flowly/
├── FlowlyApplication.kt          # Application class — สร้าง Database & Repository
├── MainActivity.kt                # Entry point — EdgeToEdge + Compose Theme
│
├── data/                          # Data Layer
│   ├── local/
│   │   ├── FlowlyDatabase.kt     # Room Database (expenses, budgets)
│   │   ├── dao/
│   │   │   ├── ExpenseDao.kt     # CRUD operations สำหรับ Expense
│   │   │   └── BudgetDao.kt      # CRUD operations สำหรับ Budget
│   │   └── entity/
│   │       ├── Expense.kt        # Entity: id, amount, category, note, date
│   │       └── Budget.kt         # Entity: income, fixed bills, usableBudget
│   └── repository/
│       └── FlowlyRepository.kt   # Repository — เป็น single source of truth
│
├── ui/                            # Presentation Layer
│   ├── components/                # Reusable UI Components
│   │   ├── AnimatedCounter.kt    # ตัวเลขเปลี่ยนค่าแบบ Animated
│   │   ├── BudgetProgressBar.kt  # วงกลม Progress แสดงสัดส่วนการใช้จ่าย
│   │   ├── DayCard.kt            # การ์ดสรุปรายจ่ายแต่ละวัน
│   │   ├── ExpenseItem.kt        # แถวรายการค่าใช้จ่าย (Swipeable)
│   │   ├── PredictionCard.kt     # การ์ดแสดงผลการพยากรณ์
│   │   └── SpendingChart.kt      # กราฟ Gradient แสดงแนวโน้มการใช้จ่าย
│   ├── navigation/
│   │   └── FlowlyNavigation.kt   # Bottom Navigation + NavHost
│   ├── screens/
│   │   ├── dashboard/            # Dashboard Screen + ViewModel
│   │   ├── addexpense/           # Add Expense Screen + ViewModel
│   │   ├── timeline/             # Timeline Screen + ViewModel
│   │   └── settings/             # Settings Screen + ViewModel
│   └── theme/
│       ├── Color.kt              # Design tokens — Dark theme palette
│       ├── Theme.kt              # Material 3 Theme configuration
│       └── Type.kt               # Typography — custom font scales
│
└── util/                          # Utility Layer
    ├── CategoryUtils.kt          # Enum หมวดหมู่ + icon/color mapping
    ├── DateUtils.kt              # Date formatting utilities
    └── PredictionEngine.kt       # อัลกอริทึมพยากรณ์งบประมาณ
```

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│  ┌───────────┐  ┌──────────┐  ┌──────────────┐ │
│  │  Screens  │──│ ViewModels│──│  Components  │ │
│  │ (Compose) │  │ (StateFlow)│  │ (Reusable)  │ │
│  └───────────┘  └──────────┘  └──────────────┘ │
│         │              │                         │
├─────────┼──────────────┼─────────────────────────┤
│         ▼              ▼                         │
│              Repository Layer                    │
│  ┌──────────────────────────────────────────┐   │
│  │          FlowlyRepository                │   │
│  │   (Single Source of Truth)               │   │
│  └──────────────────────────────────────────┘   │
│                      │                           │
├──────────────────────┼───────────────────────────┤
│                      ▼                           │
│               Data Layer                         │
│  ┌──────────────────────────────────────────┐   │
│  │         Room Database                    │   │
│  │  ┌────────────┐  ┌───────────────┐       │   │
│  │  │ ExpenseDao │  │  BudgetDao    │       │   │
│  │  └────────────┘  └───────────────┘       │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

แอปใช้สถาปัตยกรรม **MVVM** ตาม Android recommended architecture:
- **View (Compose)** — แสดงผล UI และรับ Input จากผู้ใช้
- **ViewModel** — จัดการ Business Logic + expose `StateFlow` ให้ UI observe
- **Repository** — เป็น Abstraction layer เหนือ Data Source
- **Room DAO** — จัดการ CRUD ลง SQLite Database

---

## 🎨 Design System

- **Theme**: Premium Dark Mode ด้วยโทนสี GitHub Dark
- **Primary Accent**: Purple `#6C63FF`
- **Status Colors**: Green (Safe) / Amber (Warning) / Red (Danger)
- **Typography**: Custom Material 3 typography scales
- **Animations**: Fade + Slide-in transitions, Animated counters
- **Navigation**: Bottom Navigation Bar พร้อม Rounded corners + FAB-style Add button
- **Cards**: Rounded corners (20-24dp) ด้วย Elevated dark surfaces

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug (2024.2.1) หรือใหม่กว่า
- **JDK 11** ขึ้นไป
- **Android SDK** API 36

### Installation

1. **Clone repository**
   ```bash
   git clone https://github.com/<your-username>/Flowly.git
   ```

2. **เปิดโปรเจ็คใน Android Studio**
   ```
   File → Open → เลือกโฟลเดอร์ Flowly
   ```

3. **Sync Gradle**
   - Android Studio จะ sync dependencies อัตโนมัติ
   - หรือคลิก `Sync Now` ที่ notification bar

4. **Run**
   - เลือก Emulator หรือ Physical Device (API 24+)
   - กด ▶️ Run 'app'

---

## 📦 Dependencies

```kotlin
// Jetpack Compose (BOM 2024.09.00)
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.material.icons.extended)

// Navigation
implementation(libs.androidx.navigation.compose)

// Lifecycle + ViewModel
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.compose)

// Room Database
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)

// Testing
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
```

---

## 📄 License

This project is for educational purposes.
