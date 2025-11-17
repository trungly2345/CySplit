# UserGroup API - Postman Testing Guide

Base URL: `http://localhost:8080`

---

## OVERVIEW

The UserGroup system manages the many-to-many relationship between Users and Groups with role-based permissions.

**Roles:**
- **ADMIN** - Can invite users, remove members, change roles, manage bills
- **MEMBER** - Can view group info, contribute to bills, leave group

**Key Features:**
- Role-based access control
- Invitation acceptance workflow
- Contribution tracking
- Active/inactive member status
- Admin protection (last admin cannot leave)

---

## 1. GET USER'S GROUPS | Get all groups a user belongs to

Endpoint: `GET /users/{userId}/groups`

Example: `GET /users/1/groups`

Headers:
(No special headers required)

Success Response (200 OK):
```json
[
  {
    "id": 1,
    "user": {"id": 1, "userName": "john_doe"},
    "group": {"group_id": 5, "group_name": "Weekend Trip"},
    "role": "ADMIN",
    "joinedDate": "2025-10-20T10:30:00",
    "totalContribution": 150.50,
    "isActive": true
  }
]
```

Error Response (404 Not Found):
User not found

---

## 2. GET USER'S ACTIVE GROUPS | Get only active groups for a user

Endpoint: `GET /users/{userId}/groups/active`

Example: `GET /users/1/groups/active`

Headers:
(No special headers required)

Success Response (200 OK):
```json
[
  {
    "id": 1,
    "role": "ADMIN",
    "joinedDate": "2025-10-20T10:30:00",
    "totalContribution": 150.50,
    "isActive": true
  }
]
```

---

## 3. GET GROUP MEMBERS | Get all members of a group

Endpoint: `GET /groups/{groupId}/members`

Example: `GET /groups/5/members`

Headers:
(No special headers required)

Success Response (200 OK):
```json
[
  {
    "id": 1,
    "user": {"id": 1, "userName": "john_doe"},
    "role": "ADMIN",
    "joinedDate": "2025-10-20T10:30:00",
    "totalContribution": 150.50,
    "isActive": true
  },
  {
    "id": 2,
    "user": {"id": 2, "userName": "jane_smith"},
    "role": "MEMBER",
    "joinedDate": "2025-10-21T14:20:00",
    "totalContribution": 75.00,
    "isActive": true
  }
]
```

Error Response (404 Not Found):
Group not found

---

## 4. GET ACTIVE GROUP MEMBERS | Get only active members

Endpoint: `GET /groups/{groupId}/members/active`

Example: `GET /groups/5/members/active`

Headers:
(No special headers required)

Success Response (200 OK):
```json
[
  {
    "id": 1,
    "user": {"id": 1, "userName": "john_doe"},
    "role": "ADMIN",
    "totalContribution": 150.50
  }
]
```

---

## 5. GET GROUP ADMINS | Get all admins of a group

Endpoint: `GET /groups/{groupId}/admins`

Example: `GET /groups/5/admins`

Headers:
(No special headers required)

Success Response (200 OK):
```json
[
  {
    "id": 1,
    "user": {"id": 1, "userName": "john_doe"},
    "role": "ADMIN",
    "joinedDate": "2025-10-20T10:30:00"
  }
]
```

---

## 6. GET MEMBER COUNT | Get count of active members in a group

Endpoint: `GET /groups/{groupId}/members/count`

Example: `GET /groups/5/members/count`

Headers:
(No special headers required)

Success Response (200 OK):
```json
5
```

---

## 7. ADD MEMBER TO GROUP | Directly add a user to a group (requires admin)

Endpoint: `POST /groups/{groupId}/members?userId={userId}&role={role}`

Example: `POST /groups/5/members?userId=2&role=MEMBER`

Headers:
(No special headers required)

Query Parameters:
- userId (required): ID of user to add
- role (optional): ADMIN or MEMBER (default: MEMBER)

Success Response (201 Created):
```json
{
  "id": 2,
  "user": {"id": 2, "userName": "jane_smith"},
  "group": {"group_id": 5, "group_name": "Weekend Trip"},
  "role": "MEMBER",
  "joinedDate": "2025-10-24T10:30:00",
  "totalContribution": 0.0,
  "isActive": true
}
```

Error Responses:
- 404 Not Found: User or group not found
- 409 Conflict: User already in group
- 400 Bad Request: Invalid role

---

## 8. ACCEPT INVITATION | Accept a group invitation and join as member

Endpoint: `POST /invitations/{invitationId}/accept?userId={userId}`

Example: `POST /invitations/3/accept?userId=2`

Headers:
(No special headers required)

Query Parameters:
- userId (required): ID of user accepting invitation

Success Response (201 Created):
```json
{
  "id": 3,
  "user": {"id": 2, "userName": "jane_smith"},
  "group": {"group_id": 5, "group_name": "Weekend Trip"},
  "role": "MEMBER",
  "joinedDate": "2025-10-24T10:30:00",
  "totalContribution": 0.0,
  "isActive": true
}
```

Error Responses:
- 404 Not Found: Invitation or user not found
- 400 Bad Request: Invitation already accepted/declined
- 403 Forbidden: Username doesn't match invitation
- 409 Conflict: User already in group

---

## 9. UPDATE MEMBER ROLE | Change a member's role (admin only)

Endpoint: `PUT /groups/{groupId}/members/{userId}/role?requesterId={requesterId}&newRole={newRole}`

Example: `PUT /groups/5/members/2/role?requesterId=1&newRole=ADMIN`

Headers:
(No special headers required)

Query Parameters:
- requesterId (required): ID of admin making the change
- newRole (required): ADMIN or MEMBER

Success Response (200 OK):
```json
{
  "id": 2,
  "user": {"id": 2, "userName": "jane_smith"},
  "role": "ADMIN",
  "joinedDate": "2025-10-21T14:20:00",
  "totalContribution": 75.00,
  "isActive": true
}
```

Error Responses:
- 403 Forbidden: Requester is not an admin
- 404 Not Found: Member not found
- 400 Bad Request: Invalid role

---

## 10. UPDATE CONTRIBUTION | Add to a member's total contribution

Endpoint: `PUT /groups/{groupId}/members/{userId}/contribution?amount={amount}`

Example: `PUT /groups/5/members/2/contribution?amount=50.00`

Headers:
(No special headers required)

Query Parameters:
- amount (required): Amount to add to contribution

Success Response (200 OK):
```json
{
  "id": 2,
  "user": {"id": 2, "userName": "jane_smith"},
  "role": "MEMBER",
  "totalContribution": 125.00,
  "isActive": true
}
```

Error Response (404 Not Found):
Member not found in group

---

## 11. REMOVE MEMBER | Remove a member from group (admin only)

Endpoint: `DELETE /groups/{groupId}/members/{userId}?requesterId={requesterId}`

Example: `DELETE /groups/5/members/2?requesterId=1`

Headers:
(No special headers required)

Query Parameters:
- requesterId (required): ID of admin removing the member

Success Response (204 No Content):
Empty body (member removed successfully)

Error Responses:
- 403 Forbidden: Requester is not an admin
- 404 Not Found: Member not found

---

## 12. LEAVE GROUP | User voluntarily leaves a group

Endpoint: `PUT /groups/{groupId}/members/{userId}/leave`

Example: `PUT /groups/5/members/2/leave`

Headers:
(No special headers required)

Success Response (204 No Content):
Empty body (user left group successfully)

Error Responses:
- 404 Not Found: Member not found
- 400 Bad Request: Last admin cannot leave (must promote another member first)

---

## TESTING WORKFLOW

### Scenario 1: Create Group and Add Admin

1. Create a group (using GroupController)
2. Add creator as ADMIN:
   ```
   POST /groups/5/members?userId=1&role=ADMIN
   ```

### Scenario 2: Invite and Accept

1. Create invitation (using GroupInvitationController):
   ```
   POST /groups/5/invitations
   Body: {"userName": "jane_smith"}
   ```
2. Accept invitation:
   ```
   POST /invitations/3/accept?userId=2
   ```
3. Verify member added:
   ```
   GET /groups/5/members
   ```

### Scenario 3: Promote Member to Admin

1. Admin promotes member:
   ```
   PUT /groups/5/members/2/role?requesterId=1&newRole=ADMIN
   ```
2. Verify new role:
   ```
   GET /groups/5/members/2
   ```

### Scenario 4: Track Contributions

1. User pays for something:
   ```
   PUT /groups/5/members/2/contribution?amount=50.00
   ```
2. Check total contributions:
   ```
   GET /groups/5/members
   ```

### Scenario 5: Member Leaves Group

1. Member leaves:
   ```
   PUT /groups/5/members/2/leave
   ```
2. Check active members:
   ```
   GET /groups/5/members/active
   ```

---

## ROLE-BASED PERMISSIONS

### ADMIN Can:
- Invite new members
- Remove members
- Change member roles
- View all members
- Add/edit bills
- View contributions
- Leave (if not last admin)

### MEMBER Can:
- View group members
- View group bills
- Contribute to bills
- Leave group anytime

### Both Can:
- View group information
- Track their own contributions
- Receive notifications

---

## ERROR CODES

- **200 OK:** Request successful
- **201 Created:** Resource created successfully
- **204 No Content:** Resource deleted/updated successfully
- **400 Bad Request:** Invalid request (bad role, last admin leaving, etc.)
- **403 Forbidden:** Insufficient permissions
- **404 Not Found:** Resource doesn't exist
- **409 Conflict:** Resource already exists (duplicate membership)

---

## SAMPLE TEST DATA

### Create Group Creator as Admin:
```
POST /groups/5/members?userId=1&role=ADMIN
```

### Add Multiple Members:
```
POST /groups/5/members?userId=2&role=MEMBER
POST /groups/5/members?userId=3&role=MEMBER
```

### Promote a Member:
```
PUT /groups/5/members/2/role?requesterId=1&newRole=ADMIN
```

### Track Contributions:
```
PUT /groups/5/members/1/contribution?amount=100.00
PUT /groups/5/members/2/contribution?amount=75.50
PUT /groups/5/members/3/contribution?amount=50.00
```
