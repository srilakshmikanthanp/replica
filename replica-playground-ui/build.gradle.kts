tasks.register<Exec>("runWeb") {
	group = "playground"
	description = "Starts the web app on http://localhost:5173"
	workingDir = projectDir
	commandLine("sh", "-c", "npm install && npm run dev")
}
