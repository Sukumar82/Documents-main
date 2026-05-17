import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import TransactionTablePage from './TransactionTablePage';

// Mock axios to prevent real HTTP calls
jest.mock('axios');

describe('TransactionTablePage', () => {

  beforeEach(() => {
    jest.clearAllMocks();
    // Silence window.alert (jsdom doesn't implement it)
    jest.spyOn(window, 'alert').mockImplementation(() => {});
  });

  // ─────────────────────────────────────────────────────────────
  // Task 1 & 2: Required form inputs are rendered
  // ─────────────────────────────────────────────────────────────

  test('renders Sort Code label and input', () => {
    render(<TransactionTablePage />);
    expect(screen.getByText(/Sort Code:/i)).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /Sort Code/i })).toBeInTheDocument();
  });

  test('renders Account Number label and input', () => {
    render(<TransactionTablePage />);
    expect(screen.getByText(/Account Number:/i)).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /Account Number/i })).toBeInTheDocument();
  });

  test('renders Start Date input (Task 2)', () => {
    render(<TransactionTablePage />);
    // Multiple elements contain "Start Date" (label + descriptive text), so use getAllByText
    expect(screen.getAllByText(/Start Date/i).length).toBeGreaterThanOrEqual(1);
    const dateInputs = document.querySelectorAll('input[type="date"]');
    expect(dateInputs.length).toBeGreaterThanOrEqual(2);
  });

  test('renders End Date input (Task 2)', () => {
    render(<TransactionTablePage />);
    // Multiple elements contain "End Date" – use getAllByText
    expect(screen.getAllByText(/End Date/i).length).toBeGreaterThanOrEqual(1);
  });

  test('renders Submit button', () => {
    render(<TransactionTablePage />);
    expect(screen.getByRole('button', { name: /Submit/i })).toBeInTheDocument();
  });

  // ─────────────────────────────────────────────────────────────
  // Validation: alert shown when sort code / account number blank
  // ─────────────────────────────────────────────────────────────

  test('shows alert when form submitted with blank sort code and account number', () => {
    render(<TransactionTablePage />);
    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));
    expect(window.alert).toHaveBeenCalledWith(
      'Sort code and account number cannot be blank'
    );
    // axios should NOT have been called
    expect(axios.put).not.toHaveBeenCalled();
  });

  test('shows alert when only sort code is provided and account number is blank', () => {
    render(<TransactionTablePage />);
    fireEvent.change(screen.getByRole('textbox', { name: /Sort Code/i }), {
      target: { name: 'sortCode', value: '53-68-92' },
    });
    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));
    expect(window.alert).toHaveBeenCalledWith(
      'Sort code and account number cannot be blank'
    );
    expect(axios.put).not.toHaveBeenCalled();
  });

  // ─────────────────────────────────────────────────────────────
  // Task 1: Balance is displayed after a successful API response
  // ─────────────────────────────────────────────────────────────

  test('displays current balance after successful API call (Task 1)', async () => {
    const mockAccountResponse = {
      sortCode: '53-68-92',
      accountNumber: '73084635',
      currentBalance: 1071.78,
      transactions: [],
    };
    axios.put.mockResolvedValueOnce({ data: mockAccountResponse });

    render(<TransactionTablePage />);

    // Fill in required fields
    fireEvent.change(screen.getByRole('textbox', { name: /Sort Code/i }), {
      target: { name: 'sortCode', value: '53-68-92' },
    });
    fireEvent.change(screen.getByRole('textbox', { name: /Account Number/i }), {
      target: { name: 'accountNumber', value: '73084635' },
    });

    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    // Wait for the balance amount to appear ("Current Balance" also appears in task description,
    // so wait for the £ prefixed amount which only renders after a successful API response)
    const balanceEl = await screen.findByText(/£1071\.78/);
    expect(balanceEl).toBeInTheDocument();
  });

  // ─────────────────────────────────────────────────────────────
  // Task 2: Date range fields are sent in the API payload
  // ─────────────────────────────────────────────────────────────

  test('includes startDate and endDate in API call when both are provided (Task 2)', async () => {
    const mockAccountResponse = {
      sortCode: '53-68-92',
      accountNumber: '73084635',
      currentBalance: 1071.78,
      transactions: [],
    };
    axios.put.mockResolvedValueOnce({ data: mockAccountResponse });

    render(<TransactionTablePage />);

    fireEvent.change(screen.getByRole('textbox', { name: /Sort Code/i }), {
      target: { name: 'sortCode', value: '53-68-92' },
    });
    fireEvent.change(screen.getByRole('textbox', { name: /Account Number/i }), {
      target: { name: 'accountNumber', value: '73084635' },
    });

    const dateInputs = document.querySelectorAll('input[type="date"]');
    fireEvent.change(dateInputs[0], {
      target: { name: 'startDate', value: '2019-04-01' },
    });
    fireEvent.change(dateInputs[1], {
      target: { name: 'endDate', value: '2019-06-01' },
    });

    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    await waitFor(() => expect(axios.put).toHaveBeenCalledTimes(1));

    const [, payload] = axios.put.mock.calls[0];
    expect(payload.startDate).toBe('2019-04-01');
    expect(payload.endDate).toBe('2019-06-01');
    expect(payload.sortCode).toBe('53-68-92');
    expect(payload.accountNumber).toBe('73084635');
  });

  test('sends null dates when date fields are empty (all transactions)', async () => {
    const mockAccountResponse = {
      sortCode: '53-68-92',
      accountNumber: '73084635',
      currentBalance: 1071.78,
      transactions: [],
    };
    axios.put.mockResolvedValueOnce({ data: mockAccountResponse });

    render(<TransactionTablePage />);

    fireEvent.change(screen.getByRole('textbox', { name: /Sort Code/i }), {
      target: { name: 'sortCode', value: '53-68-92' },
    });
    fireEvent.change(screen.getByRole('textbox', { name: /Account Number/i }), {
      target: { name: 'accountNumber', value: '73084635' },
    });

    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    await waitFor(() => expect(axios.put).toHaveBeenCalledTimes(1));

    const [, payload] = axios.put.mock.calls[0];
    expect(payload.startDate).toBeNull();
    expect(payload.endDate).toBeNull();
  });
});
