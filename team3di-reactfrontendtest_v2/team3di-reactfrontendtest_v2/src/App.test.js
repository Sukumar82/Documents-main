import { render, screen } from '@testing-library/react';
import App from './App';

// Mock axios so no real HTTP calls are made when App renders
jest.mock('axios');

test('renders the banking app without crashing', () => {
  render(<App />);
});

test('renders Sort Code input on the home page', () => {
  render(<App />);
  expect(screen.getByText(/Sort Code:/i)).toBeInTheDocument();
});

test('renders Account Number input on the home page', () => {
  render(<App />);
  expect(screen.getByText(/Account Number:/i)).toBeInTheDocument();
});

test('renders date range inputs on the home page', () => {
  render(<App />);
  // Multiple elements may contain "Start Date" (label + descriptive text), so use getAllByText
  expect(screen.getAllByText(/Start Date/i).length).toBeGreaterThanOrEqual(1);
  expect(screen.getAllByText(/End Date/i).length).toBeGreaterThanOrEqual(1);
});

