# AcademicAlly
Peer-to-Peer tutoring thesis project android application made with Jetpack Compose.

# Tasks (Optional):
1. Notifications and sound (too hard to implement push notifications)
2. Truncate long results (might don't want to fetch hundred data at a time but too hard to implement pagination)
3. Activity cache store in internal storage (no suitable storage from the app use case, room not suitable for unstructured objects, datastore not suitable for complex objects)
4. Channel (like a forum where users can provide questions and answers on a question and also likes/dislikes)
5. Assessment question voting (I am not good at every course or even a tutor, question voting might help find the accuracy of question, and will be removed in database if it has less than -4 likes (or 4 dislikes))

# Tasks:
1. AcademicAlly Admin Dashboard (see AcademicAlly_Admin repository)

# Features:
1. Assessment
   1. 77 courses to check your knowledge of. BSCS 53 courses, HRS 8 courses, STEM 16 courses
   2. 3 different types of assessment (multiple choice, identification, true or false)
   3. 3 different items of assessment (5, 10, 15)
   4. Random generation of items and types before the assessment and caching it. For the users that will back or quit the app, the same type and items will be process when taking again or another assessment as long as the user not finished an assessment.
   5. Different assessment types have different evaluation. In multiple choice, your score / items must be greater than or equal to 0.75 to be eligible as tutor. In identification, your score / items must be greater than or equal to 0.6 to be eligible as tutor. In true or false, your score / items must be greater than or equal to 0.9 to be eligible as tutor.
   6. If the user have taken assessment and not logged in, the assessment result is saved on preferences temporarily and will be synced in account when he/she logged in. But do not take assessment twice that is not logged in, as the first assessment will be replaced by the next. The user can exit the app and the result will be kept until logging in or clearing data.
   7. After the user logging in, the system (dashboard) will check if the user have learning pattern. If the user have no learning pattern, he/she will be redirected to an assessment that will check his/her matching learning pattern after completing it. The learning pattern will be shown in finding tutor or profile, so when the student find tutor, he/she know if the tutor matches his/her learning pattern, preferences or style.
2. Statistics
   1. 4 different points will be collected after doing something in the app. In taking assessment, the user will get 0.1 assessment points for each correct answer you got. In sending requests, the student who send request and the tutor that received the request will get each 0.1 request points. If the tutor accept the request, the tutor and the student accepted will get 0.2 request points. In completing session, the tutor and the student in that session will get 0.5 session points. After the tutor created an assignment, the tutor will be rewarded 0.5 assignment points. The student that will take the assignment created by tutor will be rewarded 0.1 assignment points for each correct answer he/she got. There is also a total points which is the addition of the four points.
   2. 28 achievements (different on student and tutor). The achievements include completing sessions, completing assignments, creating assignments, accepting and rejecting student requests, sending tutor requests, rating students and tutors and obtaining eligible courses.
   3. Miscellaneous data are requests rejected, accepted, received, sent, sessions completed, assignments created and completed, users rated and obtained rates, badges collected and assessments completed.
3. Information
   1. Roles - Users can sign in as student or tutor. But they have the ability to change roles. They can do it even they have no eligible course in that role. But for example, the user switch to tutor role but he/she don't have course eligible as tutor, his/her info will not be shown in find tutor which is a page for finding tutors base on your courses or course you want. If the user is currently as student, but he have eligible courses as tutor, his/her info will still be shown in find tutor. Every data in users two roles (student and tutor) are independent which means they all different from sessions, messages, courses, statistics, etc. and these data will only be seen by the user base on his/her role. In behind, he/she is both student and tutor but there is current.
   2. Users can change or add their information, such as address or program, update their password, and add or change images.
4. Rating
   1. Course Rating - If the user is eligible as student in the course, his rating will be 0.0 - 2.5, and in tutor will be 2.5 - 5.0. The rating is based on the past assessments he/she have done. As what as mentioned in assessment evaluation, in multiple choice it is 0.75, it will be aligned to 0.5. For example, in score / items = 0.8 will turn to 0.6 and in score / items = 0.5 will turn to 0.3333. Everything in rating is 0 to 1 times 5.
   2. Performance Rating - The users (student and tutor) can only rate each other after completing a session. This is to ensure the tutor or student attitude, behavior, accuracy and performance in face-to-face tutoring which is assigned by the tutor the location and time after accepting a student. AcademicAlly doesn't have video conferencing feature and it is designed as face-to-face tutoring.
   3. The tutor can rate the student (optional) after completing session and creating the assignment. In order to student to rate tutor, he/she must go in the archive page, and there is a button to rate the tutor. The tutor can also rate student in archive.
   4. In leaderboard, the 20 top users are based on the performance ratings (which is done after completing session).
5. Creating session after accepting student is required. Creating assignment is required after completing session.
6. Tutors can edit unfinished created sessions.
7. Support page, where the users can ask the developer for problems such as toxic users, bugs, data loss and how to use the app.
8. Forgot Password - In login, the user should only provide the email if he/she don't know the password. And when he/she click the forgot password button, he/she should check his/her mail for a message from academically (it might be in junk or spam), and access the link and input to change the password.
9. Assessments are not fixed - when the tutors make assignments, the questions are added automatically in a pending assessment database. The pending assessments can be seen by the admin and approve it if he/she wants it. The developer is not good at every course.