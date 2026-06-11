# CountIn Development Roadmap

## Phase 1 - Foundation

✅ Authentication

* Send OTP
* Verify OTP
* JWT Authentication
* Session Management
* Logout
* Current User

---

## Phase 2 - Spaces Module (Current Focus)

### Space Management

* Create Space
* Edit Space
* View Space Details
* Delete Space (Optional)

### My Spaces

* List Spaces
* Search Spaces
* Space Switcher
* Default Space Selection

### Membership Management

* Invite Members
* Accept Invitation
* Cancel Invitation
* View Members
* Role Management

### Space Types

* PG
* Mess
* Hostel
* Co-Living
* Rental

### Space Settings

* General Information
* Notifications
* Language Settings
* Space Archive / Deactivate
---

### Member Management

* Add Member
* Edit Member
* View Member Profile

### Member Profile

* Member Status
* Emergency Contact
* App User Linking

### Document Management

* Aadhaar
* PAN
* Passport
* Other Documents
* Verification Status

### Deposit Management

* Deposit Amount
* Deposit Paid
* Deposit Refunded
* Deposit Balance

### Member Notes

* Internal Notes
* Remarks

### Member History

* Status History
* Deposit History
* Profile Change History

---

# Phase 4 - Accommodation Management

## Goal

Manage physical accommodation structure and occupancy across:

* PG
* Hostel
* Co-Living
* Rental

Mess spaces do not use Accommodation Management.

---

# Property Structure

## Buildings

Represents a physical building.

Examples:

* Building A
* Building B
* Tower 1

Features:

* Create Building
* Edit Building
* View Building
* Deactivate Building

---

## Floors

Represents a floor inside a building.

Examples:

* Ground Floor
* First Floor
* Second Floor

Features:

* Create Floor
* Edit Floor
* View Floor
* Deactivate Floor

---

## Units

Represents an apartment, flat, suite, or independent unit.

Examples:

* Flat 101
* Flat 202
* Studio A

Features:

* Create Unit
* Edit Unit
* View Unit
* Deactivate Unit

---

## Rooms

Represents a room inside a floor or unit.

Examples:

* Room 101
* Room 102
* Bedroom A

Features:

* Create Room
* Edit Room
* View Room
* Deactivate Room

---

## Beds

Represents individual occupancy slots.

Examples:

* Bed A
* Bed B
* Bed C

Features:

* Create Bed
* Edit Bed
* View Bed
* Deactivate Bed

---

# Space Type Rules

## PG

Structure:

Building
→ Floor
→ Room
→ Bed

Occupancy:

Member → Bed

---

## Hostel

Structure:

Building
→ Floor
→ Room
→ Bed

Occupancy:

Member → Bed

---

## Co-Living

Structure:

Building
→ Unit
→ Room
→ Bed

Occupancy:

Member → Bed

or

Member → Room

---

## Rental

Structure:

Building
→ Unit

Optional:

Building
→ Unit
→ Room

Occupancy:

Member → Unit

---

## Mess

Accommodation Module Not Applicable

Supported Modules:

* Members
* Meals
* Availability
* Billing

---

# Occupancy Management

## Allocate Member

Assign member to:

* Bed
* Room
* Unit

depending on Space Type.

Features:

* Allocate Member
* View Allocation
* Allocation History

---

## Transfer Member

Move member between:

* Bed to Bed
* Room to Room
* Unit to Unit

Features:

* Internal Transfer
* Transfer History

---

## Vacate Member

Mark accommodation as vacated.

Features:

* Vacate Date
* Vacate Reason
* Auto Availability Update

---

## Occupancy History

Track:

* Allocation Date
* Transfer Date
* Vacate Date
* Previous Location
* New Location

Purpose:

Audit trail of accommodation movement.

---

# Availability Management

## Available Beds

List all available beds.

Filters:

* Building
* Floor
* Room

---

## Occupied Beds

List all occupied beds.

---

## Available Rooms

List rooms with available capacity.

---

## Occupied Rooms

List fully occupied rooms.

---

## Vacant Units

List units without occupants.

---

# Accommodation Status

Supported Statuses:

## AVAILABLE

Ready for allocation.

---

## OCCUPIED

Currently occupied.

---

## RESERVED

Blocked for future allocation.

---

## MAINTENANCE

Unavailable due to maintenance.

Examples:

* Painting
* Plumbing
* Repairs

---

## BLOCKED

Unavailable for business reasons.

Examples:

* Owner Use
* Renovation
* Administrative Block

---

# Capacity Management

## Room Capacity

Examples:

* Single Sharing
* Double Sharing
* Triple Sharing
* Four Sharing

Track:

* Total Capacity
* Occupied Capacity
* Available Capacity

---

## Bed Capacity

Track:

* Total Beds
* Occupied Beds
* Available Beds

---

# Dashboard Metrics

Display:

* Total Buildings

* Total Floors

* Total Units

* Total Rooms

* Total Beds

* Occupied Beds

* Available Beds

* Occupancy Percentage

* Vacant Rooms

* Vacant Units

---

# Search & Filters

Support filtering by:

* Building
* Floor
* Unit
* Room
* Status

Search by:

* Room Number
* Unit Number
* Member Name

---

# Permissions

## OWNER

Full Access

* Create
* Edit
* Delete
* Allocate
* Transfer
* Vacate

---

## MANAGER

Operational Access

* Allocate
* Transfer
* Vacate
* View

Cannot delete structures.

---

## TENANT

Read-only

Own Allocation Only

---

## CUSTOMER

No Accommodation Access

---

## STAFF

Limited Read Access

Based on future requirements.

---

# Out Of Scope

Not part of Phase 4:

* Rent Collection
* Billing
* Payments
* Deposits
* Meal Management
* Availability Polls
* Complaints
* Maintenance Tickets

These belong to future phases.


---

## Phase 5 - Meal Management

### Menu Master

* Breakfast Menu
* Lunch Menu
* Dinner Menu

### Menu Planning

* Daily Menu Planning
* Weekly Menu Planning

### Meal Tracking

* Meal Consumption
* Special Meal Requests

### Menu History

* Daily History
* Weekly History

---

## Phase 6 - Availability Management (USP)

### Meal Availability Polls

* Breakfast Poll
* Lunch Poll
* Dinner Poll

### Response Collection

* App Responses
* WhatsApp Responses

### Headcount Engine

* Expected Breakfast Count
* Expected Lunch Count
* Expected Dinner Count

### Forecasting

* Daily Forecast
* Weekly Forecast

### Reports

* Food Wastage Report
* Attendance Report
* Consumption Trends

---

## Phase 7 - Payment Management

* Rent Collection
* Subscription Collection
* Deposit Management
* Payment History
* Payment Reminders

---

## Phase 8 - Complaints & Notices

* Complaint Management
* Notice Board
* Notifications

---

## Phase 9 - Reports & Dashboard

* Occupancy Reports
* Payment Reports
* Meal Reports
* Business Dashboard

---

## Future Enhancements

* Multi-language Support
* WhatsApp Automation
* AI Demand Prediction
* QR Meal Check-In
* UPI Integration
* Advanced Analytics
