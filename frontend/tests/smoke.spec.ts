import { test, expect } from "@playwright/test";

test("renders dashboard", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText("Fitness World")).toBeVisible();
  await expect(page.getByText("AI Coach")).toBeVisible();
});
