
function resolveRefs(jObj)
{
    var idsToObjs = [];

    // First pass, store all objects that have an @ID field, mapped to their instance (self)
    walk(jObj, idsToObjs);

    // Replace all @ref: objects with the object from the association above.
    substitute(null, null, jObj, idsToObjs);

    idsToObjs = null;
}

function walk(jObj, idsToObjs)
{
    for (var field in jObj)
    {
        var value = jObj[field];
        if (field == "@id")
        {
            idsToObjs[value] = jObj;
        }
        else if (typeof(value) == "object")
        {
            walk(value, idsToObjs);
        }
    }
}

function substitute(parent, fieldName, jObj, idsToObjs)
{
    for (var field in jObj)
    {
        var value = jObj[field];
        if (field == "@ref")
        {
            if (parent != null && fieldName != null)
            {
                parent[fieldName] = idsToObjs[jObj["@ref"]];
            }
        }
        else if (typeof(value) == "object")
        {
            substitute(jObj, field, value, idsToObjs);
        }
    }
}