class ShamUrlMappings {

	static mappings = {
		"/sham/run-fixtures"(controller: 'sham', action: 'run')
		"/sham/log-seed"(controller: 'sham', action: 'logSeed')
		"/sham/seed"(controller: 'sham', action: 'getSeed')
		"/sham/set-seed"(controller: 'sham', action: 'setSeed')
	}
}
