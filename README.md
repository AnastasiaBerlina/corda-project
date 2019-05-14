# corda-project

A simple example of corda application. This example was created for the Basics of Grid and Cloud Computing course. 

Application is for grading student's work by professors

## How to deploy

1. Clone this repository

2. Run " gradle clean" , "gradle build", "gradle jar", "gradle deployNodes".

3. Run "build\nodes\runnodes.bat".

4. Run the application with the following parametres:
--server.port=10050
--config.rpc.host=localhost
--config.rpc.port=10011 (the port of node you want to do actions with)
--config.rpc.username=user1
--config.rpc.password=test

## How to work with

go to http://localhost:10050, if you are a professor and want to grade students add following parametres to address :

/gradeWork?lecturerName=ProfB&studentName=StudentA&subject=spanish&grade=2 (for example),

if you are student you can see your grades by going to "/getGrades".


## Screenshots
![Nodes deployed](/pictures/nodes.jpg)

Nodes deployed

![grade_student](/pictures/profAstudentA.jpg)

Grading studentA by Prof A

![grade_studentB](/pictures/profAstudentB.jpg)

Grading student B by Prof A

![what cause exception](/pictures/whatcauseException.jpg)

if we try to grade students by another professor, we will get an exceptin

![exception](/pictures/exception.jpg)

than let's change parametres of launch to student A

![studentA](/pictures/studentA.jpg)

and go to /getGrades

![getstudentA](/pictures/getGradesStudA.jpg)

now let's change parametres of launch to student B

![studentB](/pictures/studentB.jpg)

and go to /getGrades

![getstudentB](/pictures/getGradesStudentB.jpg)




