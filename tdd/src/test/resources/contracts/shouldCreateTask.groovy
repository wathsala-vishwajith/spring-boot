package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create a new task"

    request {
        method POST()
        url("/api/tasks")
        headers {
            contentType(applicationJson())
        }
        body([
            title: "New Task",
            description: "New Description",
            status: "TODO"
        ])
    }

    response {
        status 201
        headers {
            contentType(applicationJson())
        }
        body([
            id: $(consumer(1), producer(regex(number()))),
            title: "New Task",
            description: "New Description",
            status: "TODO",
            createdAt: $(consumer(regex(iso8601WithOffset())), producer("2024-01-01T10:00:00Z")),
            updatedAt: $(consumer(regex(iso8601WithOffset())), producer("2024-01-01T10:00:00Z"))
        ])
    }
}
