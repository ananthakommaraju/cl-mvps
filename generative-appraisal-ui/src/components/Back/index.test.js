import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Back from './index';

const renderWithRouter = (ui, { route = '/' } = {}) => {
    window.history.pushState({}, 'Test page', route);
    return render(ui, { wrapper: BrowserRouter });
};

test('renders Back component with title', () => {
    renderWithRouter(<Back title="Test Title" />);
    expect(screen.getByText('Test Title')).toBeInTheDocument();
});

test('navigates back to home on button click', () => {
    const { container } = renderWithRouter(<Back title="Test Title" />, { route: '/test' });

    const backButton = container.querySelector('button');
    fireEvent.click(backButton);

    expect(window.location.pathname).toBe('/');
});
