# Portfolio Generator

A full-stack application built with Spring Boot (backend) and Angular (frontend) for creating and managing professional portfolios.

## Features

- **Resume upload and portfolio management**
- **User authentication**
- **Custom portfolio templates**
- **Responsive web interface**
- **AI-powered resume parsing (PDF/DOCX support)**

## Technologies Used

- **Backend:** Spring Boot, MongoDB
- **Frontend:** Angular, CSS, JavaScript

## Getting Started

### Prerequisites

- **Java 11+**
- **Node.js and npm**
- **MongoDB** (local or cloud)
- **Git**

### Installation

1. **Clone the repository:**
   git clone https://github.com/AMANVermaaa/portfolio-generator.git

2. **Backend setup:**
- Navigate to the backend folder.
- Run:
  ```
  mvn spring-boot:run
  ```
3. **Frontend setup:**
- Navigate to the frontend folder.
- Run:
  ```
  npm install
  ng serve
  ```
## OpenAI Integration

This project uses the OpenAI API to provide advanced AI features such as resume parsing and content generation. To use these features, you will need to obtain an OpenAI API key and configure it as an environment variable.

**How to Set Up OpenAI Integration:**

1. **Sign Up for an OpenAI Account**
- Go to [OpenAI’s website](https://openai.com/) and create an account if you don’t already have one.

2. **Obtain Your API Key**
- Log in to your OpenAI account.
- Navigate to the [API Keys](https://platform.openai.com/account/api-keys) page.
- Click **“Create new secret key”** to generate your API key.
- **Important:** Copy and securely store your API key, as it will not be shown again.

3. **Configure the API Key**
- **Do not add your API key to any files that are committed to Git.**
- Set the `OPENAI_API_KEY` environment variable with your API key before running the application:

   
   - **Linux/macOS:**
     ```
     export OPENAI_API_KEY=your_api_key_here
     ```
   - **Windows (Command Prompt):**
     ```
     set OPENAI_API_KEY=your_api_key_here
     ```
   
- Alternatively, you can use a `.env` file in your project root (add `.env` to `.gitignore`):
  ```
  OPENAI_API_KEY=your_api_key_here
  ```

4. **Run the Application**
- Start your application as usual. The OpenAI integration will be enabled if the API key is available.

**Note:**  
Without an OpenAI API key, the core AI-powered features such as resume categorization and parsing will not work, and the application may not function as intended.



