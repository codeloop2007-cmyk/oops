# Graph Report - oops_project  (2026-10-29)

## Corpus Check
- 170 files · ~42,227 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 11 file(s) not represented in the graph (top: (none) 6, .properties 2, .lock 1)

## Summary
- 1049 nodes · 3504 edges · 56 communities (30 shown, 26 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 129 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d61bdc92`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Appointment API Endpoints
- metro.config.js
- DoctorEmbedding
- UserRepository
- PGvectorUserType
- SpecializationResponse
- Doctor Embedding & Vector Workers
- DoctorSpecializationService.java
- PrescriptionService.java
- DoctorProfileRepository
- Expo App Manifest & Assets
- DoctorProfile
- AgentServiceTest.java
- PrescriptionService
- Address
- DoctorProfileNotFoundException
- dependencies
- package.json
- Doctor Profile & Credentialing
- EmbeddingWorker.java
- AppointmentRepository
- medical.ts
- list
- Frontend API Client & Generation
- User.java
- tsconfig.json Module
- scripts Module
- BackendApplication.java Module
- Frontend API Client & Generation
- org.springframework.transaction.annotation.Transactional
- lombok.Getter
- crypto@^1.0.1
- react-native-gesture-handler@~2.31.1
- react-native-web@~0.21.0
- @react-native-async-storage/async-storage@2.2.0 Module
- Welcome to your Expo app 👋
- SlotResponse
- FastAPI AI Search Service
- ai-service Module
- com.mediconnect:backend Module
- UserExceptionHandler.java
- README.md
- AGENTS.md
- expo-glass-effect@~56.0.4
- expo-image@~56.0.11
- axios.ts
- @expo/ui@~56.0.23
- react-native-screens@~4.26.0
- expo-font@~56.0.7
- react-native-safe-area-context

## God Nodes (most connected - your core abstractions)
1. `ErrorResponse` - 80 edges
2. `DoctorProfile` - 36 edges
3. `User` - 33 edges
4. `Appointment` - 30 edges
5. `UserRepository` - 29 edges
6. `DoctorProfileRepository` - 27 edges
7. `SpecializationResponse` - 27 edges
8. `DoctorEmbeddingRepository` - 26 edges
9. `AppointmentRepository` - 24 edges
10. `DoctorEmbedding` - 23 edges

## Surprising Connections (you probably didn't know these)
- `AppointmentController` --references--> `AppointmentService`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/controller/AppointmentController.java → backend/src/main/java/com/mediconnect/backend/feature/appointment/service/AppointmentService.java
- `Appointment` --references--> `Slot`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java → backend/src/main/java/com/mediconnect/backend/feature/slot/entity/Slot.java
- `AppointmentRepository` --references--> `Appointment`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/repository/AppointmentRepository.java → backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java
- `Prescription` --references--> `Appointment`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/prescription/entity/Prescription.java → backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java
- `DoctorNoteService` --references--> `AppointmentRepository`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/doctornote/service/DoctorNoteService.java → backend/src/main/java/com/mediconnect/backend/feature/appointment/repository/AppointmentRepository.java

## Import Cycles
- None detected.

## Communities (56 total, 26 thin omitted)

### Community 0 - "Appointment API Endpoints"
Cohesion: 0.10
Nodes (15): AppointmentController, DoctorEmbeddingController, DoctorNoteController, DoctorProfileController, DoctorSearchController, DoctorSpecializationController, PrescriptionController, ReviewController (+7 more)

### Community 1 - "metro.config.js"
Cohesion: 0.29
Nodes (5): config, { getDefaultConfig }, http, expo@~56.0.17, expo

### Community 3 - "UserRepository"
Cohesion: 0.06
Nodes (15): UserResponse, RegistrationStatus, COMPLETED, PENDING, UserAlreadyRegisteredException, UserNotFoundException, UserRepository, UserService (+7 more)

### Community 5 - "SpecializationResponse"
Cohesion: 0.16
Nodes (5): SpecializationResponse, DuplicateSpecializationException, SpecializationNotFoundException, SpecializationRepository, SpecializationService

### Community 6 - "Doctor Embedding & Vector Workers"
Cohesion: 0.09
Nodes (10): AgentRequest, AgentStructuredResponse, DoctorMatch, embed(), EmbedRequest, EmbedResponse, find_doctor(), health() (+2 more)

### Community 7 - "DoctorSpecializationService.java"
Cohesion: 0.21
Nodes (4): DoctorSpecialization, DoctorSpecializationId, DoctorSpecializationRepository, DoctorSpecializationService

### Community 8 - "PrescriptionService.java"
Cohesion: 0.22
Nodes (4): Prescription, PrescriptionNotFoundException, PrescriptionItem, PrescriptionItemNotFoundException

### Community 9 - "DoctorProfileRepository"
Cohesion: 0.17
Nodes (4): DoctorEmbeddingNotFoundException, DoctorEmbeddingRepository, DoctorEmbeddingService, DoctorProfileRepository

### Community 10 - "Expo App Manifest & Assets"
Cohesion: 0.08
Nodes (24): backgroundColor, backgroundImage, foregroundImage, monochromeImage, adaptiveIcon, predictiveBackGestureEnabled, reactCompiler, typedRoutes (+16 more)

### Community 11 - "DoctorProfile"
Cohesion: 0.18
Nodes (7): Appointment, DoctorNote, DoctorProfile, Review, Specialization, User, VideoSession

### Community 12 - "AgentServiceTest.java"
Cohesion: 0.05
Nodes (12): RestTemplateConfig, AgentController, AgentRequest, AgentResponse, DoctorMatchDto, AgentService, OpenApiConfig, DoctorSearchResponse (+4 more)

### Community 13 - "PrescriptionService"
Cohesion: 0.15
Nodes (5): PrescriptionResponse, PrescriptionRepository, PrescriptionService, PrescriptionItemResponse, PrescriptionItemRepository

### Community 14 - "Address"
Cohesion: 0.18
Nodes (3): RegisterUserRequest, UpdateUserRequest, Address

### Community 16 - "dependencies"
Cohesion: 0.10
Nodes (20): dependencies, expo-crypto@~56.0.4, expo-linking@~56.0.16, expo-router@~56.2.16, expo-splash-screen@~56.0.14, expo-status-bar@~56.0.4, expo-system-ui@~56.0.5, react@19.2.3 (+12 more)

### Community 17 - "package.json"
Cohesion: 0.12
Nodes (16): expo-constants@~56.0.22, expo-device@~56.0.4, expo-secure-store@~56.0.4, expo-symbols@~56.0.6, expo-web-browser@~56.0.6, react-dom@19.2.3, main, name (+8 more)

### Community 18 - "Doctor Profile & Credentialing"
Cohesion: 0.23
Nodes (3): DoctorNoteResponse, DoctorNoteRepository, DoctorNoteService

### Community 19 - "EmbeddingWorker.java"
Cohesion: 0.07
Nodes (4): EmbedApiResponse, EmbeddingWorker, EmbedRequest, AddressTableMigrationRunner

### Community 20 - "AppointmentRepository"
Cohesion: 0.11
Nodes (6): AppointmentResponse, AppointmentRepository, AppointmentService, ReviewResponse, ReviewRepository, ReviewService

### Community 21 - "medical.ts"
Cohesion: 0.05
Nodes (43): RFC-4122, authApi, setClientIdentityId(), medicalApi, AiAssistantScreen(), styles, SUGGESTED_QUERIES, AppointmentsScreen() (+35 more)

### Community 23 - "list"
Cohesion: 0.13
Nodes (3): VideoSessionResponse, VideoSessionRepository, VideoSessionService

### Community 24 - "Frontend API Client & Generation"
Cohesion: 0.20
Nodes (9): dotenv@^17.4.2, devDependencies, orval@^8.23.0, @types/react@~19.2.2, typescript@~6.0.3, dotenv, orval, @types/react (+1 more)

### Community 25 - "User.java"
Cohesion: 0.09
Nodes (7): AppointmentNotFoundException, SlotNotAvailableException, DoctorNoteNotFoundException, DuplicateReviewException, ReviewNotFoundException, DuplicateVideoSessionException, VideoSessionNotFoundException

### Community 26 - "tsconfig.json Module"
Cohesion: 0.25
Nodes (7): compilerOptions, paths, strict, extends, include, @/assets/*, expo/tsconfig.base

### Community 27 - "scripts Module"
Cohesion: 0.29
Nodes (7): scripts, android, generate:api, ios, lint, start, web

### Community 31 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.18
Nodes (5): CreateDoctorProfileRequest, DoctorProfileResponse, UpdateDoctorProfileRequest, DuplicateLicenseNumberException, DoctorProfileService

### Community 32 - "lombok.Getter"
Cohesion: 0.08
Nodes (17): CreateAppointmentRequest, UpdateAppointmentRequest, UpsertDoctorEmbeddingRequest, CreateDoctorNoteRequest, UpdateDoctorNoteRequest, CreatePrescriptionRequest, UpdatePrescriptionRequest, CreatePrescriptionItemRequest (+9 more)

### Community 37 - "Welcome to your Expo app 👋"
Cohesion: 0.29
Nodes (6): Get a fresh project, Get started, Join the community, Learn more, Other setup steps, Welcome to your Expo app 👋

### Community 39 - "SlotResponse"
Cohesion: 0.19
Nodes (5): SlotResponse, Slot, SlotNotFoundException, SlotRepository, SlotService

### Community 44 - "UserExceptionHandler.java"
Cohesion: 0.12
Nodes (4): SpecializationAlreadyAssignedException, SpecializationNotAssignedException, DuplicateResourceException, UserExceptionHandler

### Community 53 - "axios.ts"
Cohesion: 0.18
Nodes (8): axios@^1.18.1, ApiError, axiosInstance, customAxios(), extractErrorMessage(), env, envSchema, axios

## Knowledge Gaps
- **9 isolated node(s):** `ai-service`, `com.mediconnect:backend`, `PENDING`, `COMPLETED`, `RFC-4122` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 239 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **26 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ErrorResponse` connect `Appointment API Endpoints` to `lombok.Getter`, `AgentServiceTest.java`, `UserExceptionHandler.java`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **What connects `ai-service`, `com.mediconnect:backend`, `PENDING` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Appointment API Endpoints` be split into smaller, more focused modules?**
  _Cohesion score 0.0957983193277311 - nodes in this community are weakly interconnected._
- **Why does `DoctorProfile` connect `DoctorProfile` to `lombok.Getter`, `DoctorEmbedding`, `DoctorSpecializationService.java`, `PrescriptionService.java`, `DoctorProfileRepository`, `SlotResponse`, `EmbeddingWorker.java`, `list`, `User.java`, `.getId`, `org.springframework.transaction.annotation.Transactional`?**
  _High betweenness centrality (0.025) - this node is a cross-community bridge._
- **Should `UserRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.056535504296698326 - nodes in this community are weakly interconnected._
- **Why does `EmbeddingWorker` connect `EmbeddingWorker.java` to `DoctorProfileRepository`, `UserRepository`, `AgentServiceTest.java`, `DoctorSpecializationService.java`?**
  _High betweenness centrality (0.024) - this node is a cross-community bridge._
- **Should `Doctor Embedding & Vector Workers` be split into smaller, more focused modules?**
  _Cohesion score 0.0896551724137931 - nodes in this community are weakly interconnected._