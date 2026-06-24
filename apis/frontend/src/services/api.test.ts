import { describe, expect, it } from "vitest";
import api from "./api";

describe("cliente da API", () => {
    it("usa o prefixo do gateway por padrao", () => {
        expect(api.defaults.baseURL).toBe("/api");
    });
});
