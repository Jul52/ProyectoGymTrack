export interface IPaymentMethod {
  id?: number;
  methodName?: string;
}

export const defaultValue: Readonly<IPaymentMethod> = {};
