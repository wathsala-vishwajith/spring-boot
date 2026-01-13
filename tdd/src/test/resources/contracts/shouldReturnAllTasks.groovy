package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return all tasks"

    request {
        method GET()
        url("/api/tasks")
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            [
                id: 1,
                title: "Sample Task",
                description: "Sample Description",
                status: "TODO",
                createdAt: $(consumer(regex(iso8601WithOffset())), producer("2024-01-01T10:00:00Z")),
                updatedAt: $(consumer(regex(iso8601WithOffset())), producer("2024-01-01T10:00:00Z"))
            ]
        ])
    }
}
