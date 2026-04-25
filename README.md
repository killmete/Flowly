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

### 🔮 Prediction Engine — หัวใจหลักของแอป

Prediction Engine คือระบบพยากรณ์งบประมาณแบบ Real-time ที่ทำให้ Flowly แตกต่างจากแอปจัดการเงินทั่วไป แทนที่จะแค่บันทึกรายจ่าย — Flowly จะ **พยากรณ์อนาคตทางการเงินของคุณ** ทุกครั้งที่มีรายการใหม่

---

## 🧠 Prediction Engine — Deep Dive

### แนวคิดหลัก (Core Concept)

> "ถ้าคุณใช้จ่ายในอัตรานี้ต่อไป — เดือนนี้จะเหลือเงินเท่าไหร่?"

Prediction Engine ใช้หลักการ **Linear Extrapolation** โดยนำค่าเฉลี่ยการใช้จ่ายต่อวันที่ผ่านมา ไปคาดการณ์ค่าใช้จ่ายทั้งเดือน แล้วเปรียบเทียบกับงบที่ใช้ได้จริง (Usable Budget)

### Data Flow — ข้อมูลไหลอย่างไร

```
┌──────────────┐     ┌──────────────┐     ┌──────────────────┐
│   Settings   │     │  Room DB     │     │   DateUtils      │
│              │     │              │     │                  │
│ • Income     │────▶│ Budget Table │────▶│ • daysPassed     │
│ • Phone Bill │     │ Expense Table│     │ • totalDaysInMonth│
│ • Internet   │     │              │     │ • today()        │
│ • Subs       │     └──────┬───────┘     └────────┬─────────┘
│ • Necessities│            │                      │
└──────────────┘            ▼                      ▼
                   ┌────────────────────────────────────────┐
                   │        PredictionEngine                │
                   │                                        │
                   │  Input:                                │
                   │   • usableBudget (income − fixedBills) │
                   │   • totalSpent (sum of expenses)       │
                   │   • daysPassed (วันที่ผ่านมาในเดือนนี้) │
                   │   • totalDaysInMonth (วันทั้งหมดในเดือน)│
                   │                                        │
                   │  Output: PredictionResult              │
                   └────────────────┬───────────────────────┘
                                    │
                   ┌────────────────▼───────────────────────┐
                   │          Dashboard UI                  │
                   │  • PredictionCard (ยอดพยากรณ์ + สถานะ)  │
                   │  • BudgetProgressBar (วงกลม progress)  │
                   │  • Safe Daily Budget (งบปลอดภัย/วัน)   │
                   │  • SpendingChart (กราฟแนวโน้ม)         │
                   └────────────────────────────────────────┘
```

### ขั้นตอนการคำนวณ (Algorithm Pipeline)

Prediction Engine ทำงานเป็น pipeline 5 ขั้นตอน:

#### Step 1: คำนวณ Usable Budget (งบที่ใช้ได้จริง)

ก่อนเริ่มพยากรณ์ ระบบจะคำนวณงบที่ใช้จ่ายได้จริงในเดือนนั้น โดยหักค่าใช้จ่ายคงที่ออกก่อน:

```
Usable Budget = Monthly Income − Total Fixed Bills
Total Fixed Bills = Phone + Internet + Subscriptions + Daily Necessities
```

> ตัวอย่าง: รายรับ ฿15,000 − ค่าใช้จ่ายคงที่ ฿5,000 = **Usable Budget ฿10,000**

#### Step 2: คำนวณค่าเฉลี่ยการใช้จ่ายต่อวัน (Average Daily Spend)

```
Average Daily Spend = Total Spent ÷ Days Passed
```

> ตัวอย่าง: ใช้ไป ฿4,000 ใน 10 วัน → เฉลี่ย **฿400/วัน**

#### Step 3: พยากรณ์ค่าใช้จ่ายรวมทั้งเดือน (Predicted Monthly Spend)

ใช้หลัก **Linear Extrapolation** — ถ้าใช้จ่ายในอัตรานี้ทุกวัน ทั้งเดือนจะใช้เท่าไหร่?

```
Predicted Monthly Spend = Average Daily Spend × Total Days In Month
```

> ตัวอย่าง: ฿400/วัน × 30 วัน = **฿12,000 ต่อเดือน**

#### Step 4: คำนวณยอดคงเหลือที่คาดการณ์ (Predicted End Balance)

```
Predicted End Balance = Usable Budget − Predicted Monthly Spend
```

> ตัวอย่าง: ฿10,000 − ฿12,000 = **−฿2,000** (ใช้เกินงบ! 🔴)

#### Step 5: คำนวณงบปลอดภัยต่อวัน (Safe Daily Budget)

ตอบคำถามว่า "ตั้งแต่วันนี้จนถึงสิ้นเดือน ควรใช้วันละเท่าไหร่จึงจะพอ?"

```
Remaining = Usable Budget − Total Spent
Days Left = Total Days In Month − Days Passed
Safe Daily Budget = max(Remaining ÷ Days Left, 0)
```

> ตัวอย่าง: (฿10,000 − ฿4,000) ÷ 20 วันที่เหลือ = **฿300/วัน**

### สถานะการใช้จ่าย (Spending Status Classification)

ระบบจะจัดสถานะอัตโนมัติจาก `Predicted End Balance ÷ Usable Budget`:

| สถานะ | เงื่อนไข | ความหมาย |
|:------:|:---------|:---------|
| 🟢 **SAFE** | End Balance ≥ 20% ของ Usable Budget | กำลังไปได้ดี จะเหลือเงินเยอะ |
| 🟡 **WARNING** | End Balance อยู่ระหว่าง 0–20% | เริ่มตึง ควรระวังการใช้จ่าย |
| 🔴 **DANGER** | End Balance < 0% (ติดลบ) | กำลังใช้เกินงบ! ต้องลดค่าใช้จ่าย |

### Spending Chart — กราฟแนวโน้มการพยากรณ์

Dashboard จะแสดงกราฟ **Prediction Trend** โดยวนลูปย้อนหลังตั้งแต่วันที่ 1 ถึงวันนี้ แล้วรัน Prediction Engine ใหม่ **ทุกวัน** เพื่อดูว่า "ถ้าหยุดใช้จ่ายวันนั้น ยอด Predicted End Balance จะเป็นเท่าไหร่"

```
สำหรับแต่ละวัน (day = 1, 2, ..., วันนี้):
    spentUpToDay = ผลรวมค่าใช้จ่ายวันที่ 1 ถึง day
    prediction = PredictionEngine(usableBudget, spentUpToDay, day, totalDaysInMonth)
    chartData.add(prediction.predictedEndBalance)
```

ผลลัพธ์คือ Array ของ Predicted End Balance ที่แสดงเป็น **Gradient Line Chart** บน Dashboard:
- 📈 กราฟขึ้น → แนวโน้มดี ใช้จ่ายน้อยลง
- 📉 กราฟลง → แนวโน้มไม่ดี กำลังใช้เยอะขึ้น

### Reactive Data Pipeline

ทุกอย่างเป็น **Reactive** ผ่าน Kotlin Flow — เมื่อเพิ่มรายจ่ายใหม่:

```
Add Expense → Room DB Updated → Flow emits new data
    → combine(budget, totalSpent, todayExpenses)
    → PredictionEngine recalculates
    → UI updates automatically
```

ไม่ต้อง refresh หน้าจอ ไม่ต้องกดปุ่มอัพเดต — **ทุกตัวเลขเปลี่ยนทันทีแบบ Real-time**

### ตัวอย่างการทำงานจริง (Worked Example)

```
📋 สถานการณ์:
   เดือนเมษายน (30 วัน), วันนี้คือวันที่ 10
   รายรับ: ฿15,000
   ค่าใช้จ่ายคงที่: ฿5,000
   ใช้ไปแล้ว (10 วัน): ฿4,000

📊 ผลการพยากรณ์:
   ┌─────────────────────────────────────────┐
   │ Usable Budget    = 15,000 − 5,000      │
   │                  = ฿10,000              │
   │                                         │
   │ Avg Daily Spend  = 4,000 ÷ 10           │
   │                  = ฿400/วัน             │
   │                                         │
   │ Predicted Monthly = 400 × 30            │
   │                   = ฿12,000             │
   │                                         │
   │ Predicted End     = 10,000 − 12,000     │
   │                   = −฿2,000 ❌           │
   │                                         │
   │ Remaining         = 10,000 − 4,000      │
   │                   = ฿6,000              │
   │                                         │
   │ Safe Daily Budget = 6,000 ÷ 20          │
   │                   = ฿300/วัน ✅          │
   │                                         │
   │ Status Ratio      = −2,000 ÷ 10,000     │
   │                   = −0.20               │
   │ Status            = 🔴 DANGER            │
   └─────────────────────────────────────────┘

💡 ข้อมูลนี้บอกผู้ใช้ว่า:
   "คุณกำลังใช้เงินเฉลี่ย ฿400/วัน แต่ควรใช้แค่ ฿300/วัน
    ถ้าไม่ลดการใช้จ่าย จะใช้เกินงบ ฿2,000 เมื่อสิ้นเดือน"
```

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
