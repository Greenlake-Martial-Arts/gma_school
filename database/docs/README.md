# GMA School Database Schema Documentation

## Overview
This document describes the database schema for the Greenlake Martial Arts School Management System.

## Schema Structure

### Lookup Tables
- **move_categories**: Categories for organizing martial arts moves
- **member_types**: Student membership types (Regular, Prospect, WSD, Instructor, Workshop, Parent)
- **roles**: User roles for RBAC (ADMIN, INSTRUCTOR, VIEWER)

### Core Entities
- **students**: Student information and contact details
- **levels**: Belt/rank levels with ordering
- **moves**: Individual martial arts techniques
- **level_requirements**: Requirements for each belt level
- **student_levels**: Current belt level for each student
- **attendances**: Class session records
- **attendance_entries**: Student attendance tracking
- **student_progress**: Progress tracking for requirements

### Authentication
- **users**: Login credentials and user accounts
- **user_roles**: User-role assignments (many-to-many)

### Audit
- **audit_log**: System activity tracking

## Key Relationships

### Student Management
- Students have a member_type
- Students have a current level (belt/rank)
- Students can have a user account for login

### Progress Tracking
- Each level has multiple requirements (moves to master)
- Students track progress on each requirement
- Progress can be marked complete by instructors

### Attendance System
- Attendance sessions are created for each class
- Students are marked present/absent per session

## Files
- `schema.sql` - Complete executable schema
- `README.md` - This documentation
