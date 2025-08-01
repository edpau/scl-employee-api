# 🧠 My Decision: From `SetNull` to `Cascade`, and Later to Archiving (Soft Delete)

Initially, I considered using `onDelete = ForeignKeyAction.SetNull` for the foreign key between `contracts` and `employees`. This would mean that when an employee is deleted, their `employee_id` in the `contracts` table would be set to `NULL`.

At first, this seemed flexible — I thought maybe I could preserve contract records even after an employee is gone. But when I reflected on the actual relationship between an employee and their contracts, it became clear:

> A contract **must** belong to an employee.  
> Without an employee, the contract makes no sense.

This made me realize that allowing a `NULL` foreign key would introduce unnecessary complexity:
- I would need to handle orphaned contracts in queries.
- Every downstream logic would need to account for contracts with no owner.
- It violates the business rule that every contract should always belong to someone.

So I switched the foreign key rule to:

```scala
onDelete = ForeignKeyAction.Cascade
```

Now, when I delete an employee, all their contracts are deleted automatically. This keeps the database clean and avoids broken links or invalid references.

However, I also recognize that in the real world, it's uncommon to delete employee records outright. Usually, employees are archived instead of being removed from the database — to preserve history, audits, or reactivation.

That's why my plan is:
1. Use `Cascade` for now — it’s simple and effective for an MVP.
2. Later, I’ll replace hard deletion with **soft deletion** by adding an `is_archived: Boolean` field to the `employees` table.
3. Instead of deleting, I’ll set `is_archived = true` and filter out archived employees in queries.

This approach lets me:
- Build fast for MVP
- Learn and implement cascading delete
- Then grow the system realistically with soft-deletion logic

It reflects how real projects often evolve — starting simple, then adapting to data retention needs as the product matures.
