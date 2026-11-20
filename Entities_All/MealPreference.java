package Entities_All;

public class MealPreference {
    private String mealType;
    private String description;
    private int    calories;
    private String specialRequest;

    // All available meal options as constants
    public static final MealPreference[] ALL_MEALS = {
        new MealPreference("Vegetarian",      "Fresh vegetables, grains, dairy, and eggs. No meat.",         450),
        new MealPreference("Non-Vegetarian",  "Grilled chicken or beef with rice, salad, and dessert.",      620),
        new MealPreference("Vegan",           "100% plant-based: vegetables, legumes, tofu, fruits.",        380),
        new MealPreference("Halal",           "Certified Halal meat with rice/bread and vegetables.",        580),
        new MealPreference("Kosher",          "Certified Kosher food prepared under Jewish dietary laws.",   520),
        new MealPreference("Gluten-Free",     "Gluten-free grains, vegetables, and proteins. No wheat.",     410),
        new MealPreference("Low Calorie",     "Light, healthy meal under 400 calories with fresh produce.",  350),
        new MealPreference("Diabetic",        "Low-sugar, low-carb meal suitable for diabetic passengers.",  400),
        new MealPreference("Child Meal",      "Kid-friendly meal: pasta, nuggets, fruit, and juice.",        480),
        new MealPreference("No Meal",         "No in-flight meal selected.",                                   0),
    };

    public MealPreference(String mealType, String description, int calories) {
        this.mealType    = mealType;
        this.description = description;
        this.calories    = calories;
        this.specialRequest = "";
    }

    public String getMealType()        { return mealType; }
    public String getDescription()     { return description; }
    public int    getCalories()        { return calories; }
    public String getSpecialRequest()  { return specialRequest; }

    public void setMealType(String t)        { mealType = t; }
    public void setDescription(String d)     { description = d; }
    public void setCalories(int c)           { calories = c; }
    public void setSpecialRequest(String r)  { specialRequest = r; }

    public static String[] getMealTypeNames() {
        String[] names = new String[ALL_MEALS.length];
        for (int i = 0; i < ALL_MEALS.length; i++) names[i] = ALL_MEALS[i].getMealType();
        return names;
    }

    public static MealPreference findByType(String type) {
        for (MealPreference m : ALL_MEALS) {
            if (m.getMealType().equalsIgnoreCase(type)) return m;
        }
        return ALL_MEALS[0];
    }

    @Override
    public String toString() {
        return mealType + (calories > 0 ? " (" + calories + " kcal)" : "");
    }
}
