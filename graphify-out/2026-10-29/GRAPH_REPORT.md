# Graph Report - oops_project  (2026-10-29)

## Corpus Check
- 169 files · ~41,915 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 11 file(s) not represented in the graph (top: (none) 6, .properties 2, .lock 1)

## Summary
- 1043 nodes · 3497 edges · 72 communities (32 shown, 40 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 129 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d61bdc92`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Appointment API Endpoints
- lombok.Getter
- DoctorProfile
- list
- PGvectorUserType
- SpecializationResponse
- Doctor Embedding & Vector Workers
- DoctorSpecializationService.java
- PrescriptionService.java
- DoctorProfile
- Expo App Manifest & Assets
- PrescriptionService.java
- AgentServiceTest.java
- PrescriptionResponse
- lombok.Setter
- DoctorProfileNotFoundException
- dependencies
- package.json
- Doctor Profile & Credentialing
- EmbeddingWorker.java
- ReviewResponse
- medical.ts
- VideoSessionResponse
- Frontend API Client & Generation
- DoctorNoteService.java
- tsconfig.json Module
- scripts Module
- BackendApplication.java Module
- Frontend API Client & Generation
- expo-constants@~56.0.22 Module
- org.springframework.transaction.annotation.Transactional
- io.swagger.v3.oas.annotations.media.Schema
- Appointment
- notblank
- EmbeddingWorker
- @react-native-async-storage/async-storage@2.2.0 Module
- Welcome to your Expo app 👋
- react-native-reanimated@4.3.1 Module
- SlotResponse
- FastAPI AI Search Service
- ai-service Module
- com.mediconnect:backend Module
- UserExceptionHandler.java
- README.md
- AGENTS.md
- UserLanguageResponse
- expo-glass-effect@~56.0.4
- expo-image@~56.0.11
- expo-linking@~56.0.16
- DoctorSearchService
- axios.ts
- @expo/ui@~56.0.23
- react-native-screens@~4.26.0
- react-native-worklets@0.8.3
- .ask
- OpenApiConfig.java
- Address.java
- CreateReviewRequest
- EmbedApiResponse
- CreatePrescriptionItemRequest
- UpsertDoctorEmbeddingRequest
- DuplicateResourceException
- expo-device@~56.0.4
- expo-font@~56.0.7
- expo-web-browser@~56.0.6
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
- `AgentService` --references--> `DoctorSearchService`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/agent/service/AgentService.java → backend/src/main/java/com/mediconnect/backend/feature/doctorsearch/service/DoctorSearchService.java
- `AppointmentController` --references--> `AppointmentService`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/controller/AppointmentController.java → backend/src/main/java/com/mediconnect/backend/feature/appointment/service/AppointmentService.java
- `Appointment` --references--> `Review`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java → backend/src/main/java/com/mediconnect/backend/feature/review/entity/Review.java
- `Appointment` --references--> `Slot`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java → backend/src/main/java/com/mediconnect/backend/feature/slot/entity/Slot.java
- `Appointment` --references--> `User`  [EXTRACTED]
  backend/src/main/java/com/mediconnect/backend/feature/appointment/entity/Appointment.java → backend/src/main/java/com/mediconnect/backend/feature/user/entity/User.java

## Import Cycles
- None detected.

## Communities (72 total, 40 thin omitted)

### Community 0 - "Appointment API Endpoints"
Cohesion: 0.09
Nodes (17): AppointmentController, DoctorEmbeddingController, DoctorNoteController, DoctorProfileController, DoctorSearchController, DoctorSpecializationController, PrescriptionController, ReviewController (+9 more)

### Community 1 - "lombok.Getter"
Cohesion: 0.15
Nodes (4): DoctorSearchResponse, UpdatePrescriptionRequest, UpdatePrescriptionItemRequest, UpdateSlotRequest

### Community 2 - "DoctorProfile"
Cohesion: 0.12
Nodes (5): DoctorEmbedding, User, UserLanguage, UserLanguageAlreadyExistsException, UserLanguageNotFoundException

### Community 3 - "list"
Cohesion: 0.05
Nodes (16): DoctorEmbeddingRepository, DoctorEmbeddingService, DoctorProfileRepository, PrescriptionRepository, PrescriptionService, PrescriptionItemRepository, RegistrationStatus, COMPLETED (+8 more)

### Community 5 - "SpecializationResponse"
Cohesion: 0.16
Nodes (5): Specialization, DuplicateSpecializationException, SpecializationNotFoundException, SpecializationRepository, SpecializationService

### Community 6 - "Doctor Embedding & Vector Workers"
Cohesion: 0.09
Nodes (10): AgentRequest, AgentStructuredResponse, DoctorMatch, embed(), EmbedRequest, EmbedResponse, find_doctor(), health() (+2 more)

### Community 7 - "DoctorSpecializationService.java"
Cohesion: 0.25
Nodes (3): DoctorSpecialization, DoctorSpecializationId, DoctorSpecializationRepository

### Community 8 - "PrescriptionService.java"
Cohesion: 0.22
Nodes (4): Prescription, PrescriptionNotFoundException, PrescriptionItem, PrescriptionItemNotFoundException

### Community 9 - "DoctorProfile"
Cohesion: 0.14
Nodes (5): SlotNotAvailableException, DoctorEmbeddingNotFoundException, DoctorProfile, Slot, SlotNotFoundException

### Community 10 - "Expo App Manifest & Assets"
Cohesion: 0.08
Nodes (24): backgroundColor, backgroundImage, foregroundImage, monochromeImage, adaptiveIcon, predictiveBackGestureEnabled, reactCompiler, typedRoutes (+16 more)

### Community 12 - "AgentServiceTest.java"
Cohesion: 0.16
Nodes (3): AgentController, AgentService, AgentServiceTest

### Community 14 - "lombok.Setter"
Cohesion: 0.22
Nodes (5): UpdateAppointmentRequest, RegisterUserRequest, UpdateUserRequest, Address, UpdateVideoSessionRequest

### Community 15 - "DoctorProfileNotFoundException"
Cohesion: 0.20
Nodes (3): DoctorEmbeddingResponse, DoctorProfileNotFoundException, DoctorSpecializationService

### Community 16 - "dependencies"
Cohesion: 0.11
Nodes (18): dependencies, crypto@^1.0.1, expo-crypto@~56.0.4, expo-router@~56.2.16, expo-status-bar@~56.0.4, expo-system-ui@~56.0.5, react@19.2.3, react-dom@19.2.3 (+10 more)

### Community 17 - "package.json"
Cohesion: 0.13
Nodes (14): expo@~56.0.17, expo-secure-store@~56.0.4, expo-splash-screen@~56.0.14, expo-symbols@~56.0.6, @tanstack/react-query@^5.101.4, main, name, private (+6 more)

### Community 18 - "Doctor Profile & Credentialing"
Cohesion: 0.22
Nodes (4): DoctorNoteResponse, DoctorNote, DoctorNoteRepository, DoctorNoteService

### Community 20 - "ReviewResponse"
Cohesion: 0.21
Nodes (4): ReviewResponse, Review, ReviewRepository, ReviewService

### Community 21 - "medical.ts"
Cohesion: 0.05
Nodes (43): RFC-4122, authApi, setClientIdentityId(), medicalApi, AiAssistantScreen(), styles, SUGGESTED_QUERIES, AppointmentsScreen() (+35 more)

### Community 24 - "Frontend API Client & Generation"
Cohesion: 0.20
Nodes (9): dotenv@^17.4.2, devDependencies, orval@^8.23.0, @types/react@~19.2.2, typescript@~6.0.3, dotenv, orval, @types/react (+1 more)

### Community 25 - "DoctorNoteService.java"
Cohesion: 0.20
Nodes (4): AppointmentNotFoundException, DoctorNoteNotFoundException, DuplicateVideoSessionException, VideoSessionNotFoundException

### Community 26 - "tsconfig.json Module"
Cohesion: 0.25
Nodes (7): compilerOptions, paths, strict, extends, include, @/assets/*, expo/tsconfig.base

### Community 27 - "scripts Module"
Cohesion: 0.29
Nodes (7): scripts, android, generate:api, ios, lint, start, web

### Community 31 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.27
Nodes (3): DoctorProfileResponse, DoctorProfileService, SpecializationResponse

### Community 32 - "io.swagger.v3.oas.annotations.media.Schema"
Cohesion: 0.16
Nodes (4): CreateAppointmentRequest, CreateDoctorNoteRequest, CreateSlotRequest, CreateVideoSessionRequest

### Community 33 - "Appointment"
Cohesion: 0.23
Nodes (4): Appointment, AppointmentRepository, DuplicateReviewException, ReviewNotFoundException

### Community 34 - "notblank"
Cohesion: 0.16
Nodes (3): UpdateDoctorNoteRequest, CreateSpecializationRequest, AddUserLanguageRequest

### Community 37 - "Welcome to your Expo app 👋"
Cohesion: 0.29
Nodes (6): Get a fresh project, Get started, Join the community, Learn more, Other setup steps, Welcome to your Expo app 👋

### Community 39 - "SlotResponse"
Cohesion: 0.14
Nodes (5): AppointmentResponse, AppointmentService, SlotResponse, SlotRepository, SlotService

### Community 44 - "UserExceptionHandler.java"
Cohesion: 0.12
Nodes (4): DuplicateLicenseNumberException, SpecializationAlreadyAssignedException, SpecializationNotAssignedException, UserExceptionHandler

### Community 52 - "DoctorSearchService"
Cohesion: 0.24
Nodes (3): DoctorSearchService, EmbedRequest, EmbedResponse

### Community 53 - "axios.ts"
Cohesion: 0.19
Nodes (8): axios@^1.18.1, ApiError, axiosInstance, customAxios(), extractErrorMessage(), env, envSchema, axios

### Community 60 - ".ask"
Cohesion: 0.33
Nodes (3): AgentRequest, AgentResponse, DoctorMatchDto

## Knowledge Gaps
- **9 isolated node(s):** `ai-service`, `com.mediconnect:backend`, `PENDING`, `COMPLETED`, `RFC-4122` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 234 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **40 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ErrorResponse` connect `Appointment API Endpoints` to `io.swagger.v3.oas.annotations.media.Schema`, `lombok.Getter`, `UserExceptionHandler.java`, `lombok.Setter`, `.ask`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **What connects `ai-service`, `com.mediconnect:backend`, `PENDING` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Appointment API Endpoints` be split into smaller, more focused modules?**
  _Cohesion score 0.08784448818897637 - nodes in this community are weakly interconnected._
- **Why does `EmbeddingWorker` connect `EmbeddingWorker` to `EmbedApiResponse`, `list`, `DoctorSpecializationService.java`, `AgentServiceTest.java`, `EmbeddingWorker.java`?**
  _High betweenness centrality (0.038) - this node is a cross-community bridge._
- **Should `lombok.Getter` be split into smaller, more focused modules?**
  _Cohesion score 0.14705882352941177 - nodes in this community are weakly interconnected._
- **Why does `DoctorProfile` connect `DoctorProfile` to `lombok.Getter`, `DoctorProfile`, `EmbeddingWorker`, `list`, `DoctorSpecializationService.java`, `PrescriptionService.java`, `PrescriptionService.java`, `lombok.Setter`, `Doctor Profile & Credentialing`, `EmbeddingWorker.java`, `DoctorNoteService.java`, `Address.java`, `org.springframework.transaction.annotation.Transactional`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Should `DoctorProfile` be split into smaller, more focused modules?**
  _Cohesion score 0.11954022988505747 - nodes in this community are weakly interconnected._