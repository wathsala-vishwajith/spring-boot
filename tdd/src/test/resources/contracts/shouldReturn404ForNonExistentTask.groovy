package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return 404 for non-existent task"

    request {
        method GET()
        url("/api/tasks/999")
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status 404
        headers {
            contentType(applicationJson())
        }
        body([
            timestamp: $(consumer(regex(iso8601WithOffset())), producer("2024-01-01T10:00:00Z")),
            status: 404,
            error: "Not Found",
            message: "Task not found with id: 999"
        ])
    }
}
