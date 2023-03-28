db.createUser(
    {
        user: "lottoWebUser",
        pwd: "lottoWebPassword",
        roles: [
            {
                role: "readWrite",
                db: "lottoWebDataBase"
            }
        ]
    }
)
