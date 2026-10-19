pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        stage('Verify JAR') {
            steps {
                sh '''
                    echo "=== Проверка наличия собранного JAR-файла ==="
                    ls -la target/*.jar

                    if ls target/*.jar 1> /dev/null 2>&1; then
                        echo "УСПЕХ: JAR-файл успешно найден в каталоге target!"
                    else
                        echo "ОШИБКА: JAR-файл не найден!"
                        exit 1
                    fi
                '''
            }
        }
    }
}
