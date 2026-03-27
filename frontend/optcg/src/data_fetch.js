export async function card_data_fetch() {
  try {
    const response = await fetch('https://cdn.cardkaizoku.com/card_data.json');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    // Convert the response to a Blob
    const blob = await response.blob();
    
    // Create a temporary URL for the Blob
    const url = window.URL.createObjectURL(blob);
    
    // Create a temporary anchor element and trigger download
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = 'card_data.json';
    document.body.appendChild(a);
    a.click();
    
    // Cleanup
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    
    return true;
  } catch (error) {
    console.error('Error fetching card data:', error);
    throw error;
  }
}
