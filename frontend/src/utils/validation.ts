// Form validation utilities

export const validateEmail = (email: string): string | null => {
  if (!email) return "Email is required";
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) return "Invalid email format";
  return null;
};

export const validatePassword = (password: string): string | null => {
  if (!password) return "Password is required";
  if (password.length < 6) return "Password must be at least 6 characters";
  if (!/[A-Z]/.test(password))
    return "Password must contain at least one uppercase letter";
  if (!/[a-z]/.test(password))
    return "Password must contain at least one lowercase letter";
  if (!/[0-9]/.test(password))
    return "Password must contain at least one number";
  if (!/[@$!%*?&#]/.test(password))
    return "Password must contain at least one special character";
  return null;
};

export const validateRequired = (
  value: string,
  fieldName: string
): string | null => {
  if (!value || !value.trim()) return `${fieldName} is required`;
  return null;
};

export const validateUsername = (username: string): string | null => {
  if (!username) return "Username is required";
  if (username.length < 3) return "Username must be at least 3 characters";
  if (username.length > 20) return "Username must be at most 20 characters";
  if (!/^[a-zA-Z0-9_]+$/.test(username)) {
    return "Username can only contain letters, numbers, and underscores";
  }
  return null;
};

export const validatePaymentCard = (cardNumber: string): string | null => {
  if (!cardNumber) return "Card number is required";
  const cleaned = cardNumber.replace(/\s/g, "");
  if (!/^\d{13,19}$/.test(cleaned)) return "Invalid card number";

  // Luhn algorithm for card validation
  let sum = 0;
  let isEven = false;

  for (let i = cleaned.length - 1; i >= 0; i--) {
    let digit = parseInt(cleaned[i]);

    if (isEven) {
      digit *= 2;
      if (digit > 9) {
        digit -= 9;
      }
    }

    sum += digit;
    isEven = !isEven;
  }

  if (sum % 10 !== 0) return "Invalid card number";
  return null;
};
